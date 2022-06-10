package com.thuypham.ptithcm.learningrxjava.model

import com.thuypham.ptithcm.learningrxjava.util.IMAGE_URL


data class MovieList(
    val dates: Dates?,
    val page: Int?,
    val results: ArrayList<Movie>?,
    val total_pages: Int?,
    val total_results: Int?
)

data class Dates(
    val maximum: String?,
    val minimum: String?
)

data class Movie(
    val adult: Boolean?,
    val backdrop_path: String?,
    val genre_ids: List<Int>?,
    val id: Int?,
    val original_language: String?,
    val original_title: String?,
    val overview: String?,
    val popularity: Double?,
    val poster_path: String?,
    val release_date: String?,
    val title: String?,
    val video: Boolean?,
    val vote_average: Double?,
    val vote_count: Int?
) {
    fun imageUrl() = if (poster_path != null) IMAGE_URL.format(poster_path)
    else IMAGE_URL.format(backdrop_path)
}