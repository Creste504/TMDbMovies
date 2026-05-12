package com.example.tmdbmovies.model

import com.google.gson.annotations.SerializedName

// Cada 'Movie' é um filme individual
data class Movie(
    val id: Int,
    val title: String,
    @SerializedName("poster_path") val posterPath: String?, // 'poster_path' vira 'posterPath'
    @SerializedName("vote_average") val voteAverage: Double,
    val overview: String?
)

// 'MovieResponse' é o envelope que traz a lista 'results'
data class MovieResponse(
    val results: List<Movie>
)