package com.thuypham.ptithcm.learningrxjava.service

import android.util.Log
import com.thuypham.ptithcm.learningrxjava.model.MovieGenres
import com.thuypham.ptithcm.learningrxjava.model.MovieList
import com.thuypham.ptithcm.learningrxjava.util.BASE_URL
import com.thuypham.ptithcm.learningrxjava.util.DEFAULT_LANGUAGE
import com.thuypham.ptithcm.learningrxjava.util.TOKEN_API
import io.reactivex.Observable
import io.reactivex.Single
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("movie/now_playing")
    fun getListMovie(
        @Query("api_key") token: String = TOKEN_API,
        @Query("page") page: Int = 1
    ): Observable<MovieList>

    @GET("genre/movie/list")
    fun getMovieGenres(
        @Query("api_key") apiKey: String = TOKEN_API,
        @Query("language") language: String = DEFAULT_LANGUAGE,
    ): Observable<MovieGenres>

    @GET("genre/movie/list")
    fun getMovieGenres1(
        @Query("api_key") apiKey: String = TOKEN_API,
        @Query("language") language: String = DEFAULT_LANGUAGE,
    ): Single<MovieGenres>

    @GET("movie/{movie_id}/lists")
    fun getListMovie(
        @Path("movie_id") movieID: Int,
        @Query("api_key") apiKey: String = TOKEN_API,
        @Query("language") language: String = DEFAULT_LANGUAGE,
    ): Observable<MovieList?>

    companion object {

        val logging = HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
            override fun log(message: String) {
                Log.d("API", message)
            }
        }).setLevel(HttpLoggingInterceptor.Level.BASIC)

        var client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        val api = Retrofit
            .Builder()
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)

    }
}