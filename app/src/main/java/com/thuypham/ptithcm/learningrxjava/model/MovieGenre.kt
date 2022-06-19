package com.thuypham.ptithcm.learningrxjava.model


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable


@Parcelize
data class MovieGenres(
    @SerializedName("genres")
    var genres: List<MovieGenre>?
) : Parcelable

@Parcelize
data class MovieGenre(
    @SerializedName("id")
    var id: Int?,
    @SerializedName("name")
    var name: String?
) : Parcelable