package com.example.tmdbmovies

data class MovieRating(
    val movieId: Int = 0,
    val ratingGiven: Float = 0.0f,
    val movieTitle: String = "",
    val posterPath: String? = ""
)