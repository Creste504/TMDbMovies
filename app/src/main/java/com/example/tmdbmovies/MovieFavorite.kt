package com.example.tmdbmovies

// Classe limpa para o Firebase, sem anotações chatas de Room
data class MovieFavorite(
    val id: Int = 0,
    val title: String = "",
    val posterPath: String? = "",
    val overview: String = "",
    val rating: Double = 0.0,
    val releaseDate: String? = ""
)