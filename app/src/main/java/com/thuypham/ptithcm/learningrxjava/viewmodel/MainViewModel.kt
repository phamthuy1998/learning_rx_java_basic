package com.thuypham.ptithcm.learningrxjava.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.thuypham.ptithcm.learningrxjava.model.GenreMovieList
import com.thuypham.ptithcm.learningrxjava.model.Movie
import com.thuypham.ptithcm.learningrxjava.model.MovieGenre
import com.thuypham.ptithcm.learningrxjava.model.ResponseHandler
import com.thuypham.ptithcm.learningrxjava.service.ApiService
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class MainViewModel : ViewModel() {

    /*
    * doOnSubscribe() : sửa đổi nguồn source để nó gọi action được chỉ định ngay khi được subscribe.
    * doOnTerminate() : gọi action được chỉ định ngay trước khi Observable thông báo onError() hoặc onCompleted()
    * doAfterTerminate() : gọi hành động được chỉ định ngay sau khi Observable rơi vào onError() hoặc onCompleted()
    * doFinally() : gọi hành động được chỉ định sau khi Observable rơi vào onError() hoặc onCompleted() hoặc bị disposed bởi downstream.
    *
    * */
    private var compositeDisposable = CompositeDisposable()

    private val _data = MutableLiveData<ResponseHandler<ArrayList<Movie>>>()
    val movieResponse: LiveData<ResponseHandler<ArrayList<Movie>>> = _data
    private val movieList = ArrayList<Movie>()
    private var currentPage = 1

    fun getData() {
        compositeDisposable.add(
            ApiService.api.getListMovie(page = currentPage)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe {
                    _data.value = (ResponseHandler.Loading)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    currentPage++
                    it.results?.let { it1 -> movieList.addAll(it1) }
                    _data.value = (ResponseHandler.Success(movieList))
                }, {
                    _data.value = (ResponseHandler.Failure(it, it.message))
                })
        )

    }

    /*
    * .debounce(200, TimeUnit.MILLISECONDS) Delay 1 khoảng thời gian
    * Map: Map sẽ chuyển đổi các item được phát ra bởi 1 Observable
    *  bằng cách áp dụng mỗi hàm cho mỗi item,
    *  dễ hiểu hơn thì nó dùng để chuyển đối 1 item thành 1 item khác.
    *
    *
    * flatMap: FlatMap sẽ chuyển đổi các item phát ra bởi một Observable thành các Observable khác
    * flatMap sẽ không quan tâm đến thứ tự của các phần tử. Nó sẽ tạo một Observable mới cho
    * mỗi phần tử và không liên quan gì đến nhau
    *
    * ==> điểm khác biệt chính giữa Map và FlatMap là FlatMap bản thân nó sẽ trả về một Observable.
    * Nó được dùng để map trên các hoạt động bất đồng bộ.
    *
    *
    * SwitchMap: Khi phần từ mới được emit, nó sẽ hủy (unsubcribe) Observable được tạo ra trước đó và sẽ chạy Observable mới.
    *
    * ConcatMap: Tương tự flatmap nhưng giữ được thứ tự, tuy nhiên nó phá vỡ cơ chế bất đồng bộ và làm quá trình chạy chậm hơn
    *
    *flatMap: không quan tâm tới sự sắp xếp của items, chạy bất đồng bộ.
    switchMap: unsubcribe observable trước đó sau khi emit một cái mới
    concatMap: giữ nguyên sắp xếp các items, chạy đồng bộ.
    *
    *
      .takeUntil {
            it.results.isNullOrEmpty()
        }
        --> repeat call util condition...
        *
        * groupBy:
        *
        *     *  */
    private var genres: ArrayList<MovieGenre> = arrayListOf()

    fun getGenresData() {
        compositeDisposable.add(
            ApiService.api.getMovieGenres()
                .map {
                    it.genres?.let { it1 -> genres.addAll(it1) }
                    it.genres
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext {
                    Log.d(TAG, "getGenresData: $it")
                    it?.let { it1 -> getMovieByGenreID(it1) }
                }
                .doOnError {

                }
                .subscribe()
        )
    }

//    val hashMap: HashMap<String, ArrayList<Movie>?> = HashMap()

    fun getGenresData1() {
        compositeDisposable.add(
            ApiService.api.getMovieGenres()
                .flatMap {
                    Observable.fromIterable(it.genres)
                        .doOnError {
                            Log.e(TAG, "getGenresData1: fromIterable error ${it.printStackTrace()}")
                        }
                }
                .doOnError {
                    Log.e(TAG, "getGenresData1: $it")
                }.flatMap { movieResponse ->
                    ApiService.api.getListMovieByGenreID(movieResponse.id ?: 0)
                        .map {
//                            hashMap.put(movieResponse.name ?: "", it.results)
                            GenreMovieList(movieResponse, it.results)
                        }
                        .doOnError {
                            Log.e(TAG, "getGenresData1: getListMovie error ${it.printStackTrace()}")
                        }

                }

                .collectInto(ArrayList()) { listMovieByGenre: ArrayList<GenreMovieList>, items ->
                    if (items != null) {
                        listMovieByGenre.add(items)
                    }
                }
                .doOnSuccess {
                    Log.d(TAG, "getGenresData1: result $it")
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError {
                    Log.e(TAG, "getGenresData1: error ${it.printStackTrace()}")
                }
                .subscribe({

                },{
                    Log.e(TAG, "getGenresData1: subcribe error ${it.printStackTrace()}")
                })
        )
    }

    private fun getMovieByGenreID(genres: List<MovieGenre>) {
        Log.d(TAG, "getCategoryById:-enter")
        compositeDisposable.add(
            Observable.fromIterable(genres)
                .flatMap {
                    Log.d(TAG, "getCategoryById: getListItem: ${it.id}")
                    ApiService.api.getListMovie(it.id ?: 0)
                        .onErrorResumeNext(Observable.just(null))
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .toList()
                .subscribe({
                    Log.d(TAG, "getCategoryById: list: ${it.size}")

                }, {
                    Log.e(TAG, "getCategoryById: list: $it")
                })
        )
    }

    fun testRx() {

    }

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }

    companion object {
        private val TAG = MainViewModel::class.java.simpleName
    }
}