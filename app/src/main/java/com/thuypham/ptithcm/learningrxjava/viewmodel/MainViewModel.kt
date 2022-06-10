package com.thuypham.ptithcm.learningrxjava.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.thuypham.ptithcm.learningrxjava.model.Movie
import com.thuypham.ptithcm.learningrxjava.model.ResponseHandler
import com.thuypham.ptithcm.learningrxjava.service.ApiService
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.*
import kotlin.collections.ArrayList

class MainViewModel : ViewModel() {

    /*
    * doOnSubscribe() : sửa đổi nguồn source để nó gọi action được chỉ định ngay khi được subscribe.
    * doOnTerminate() : gọi action được chỉ định ngay trước khi Observable thông báo onError() hoặc onCompleted()
    * doAfterTerminate() : gọi hành động được chỉ định ngay sau khi Observable rơi vào onError() hoặc onCompleted()
    * doFinally() : gọi hành động được chỉ định sau khi Observable rơi vào onError() hoặc onCompleted() hoặc bị disposed bởi downstream.
    */
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
                    _data.value =(ResponseHandler.Loading)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    currentPage++
                    it.results?.let { it1 -> movieList.addAll(it1) }
                    _data.value =(ResponseHandler.Success(movieList))
                }, {
                    _data.value =(ResponseHandler.Failure(it, it.message))
                })
        )

    }

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }
}