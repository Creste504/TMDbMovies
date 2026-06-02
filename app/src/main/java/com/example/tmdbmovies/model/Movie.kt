package com.example.tmdbmovies.model

import com.google.gson.annotations.SerializedName

// Cada 'Movie' é um filme individual
data class Movie(
    val id: Int,
    val title: String,
    @SerializedName("poster_path") val posterPath: String?, // 'poster_path' vira 'posterPath'
    @SerializedName("vote_average") val voteAverage: Double,
    val overview: String?,
    @SerializedName("release_date") val releaseDate: String? // ADICIONADO: Agora o modelo sabe ler a data da API!
) : java.io.Serializable

// 'MovieResponse' é o envelope que traz a lista 'results'
data class MovieResponse(
    val results: List<Movie>
)
data class MovieDetailsResponse(
    val runtime: Int?, // Duração em minutos
    val genres: List<MovieGenre>
)

data class MovieGenre(
    val id: Int,
    val name: String
)

// Dados do Elenco
data class MovieCreditsResponse(
    val cast: List<CastMember>
)

data class CastMember(
    val name: String,
    @SerializedName("profile_path") val profilePath: String?,
    val character: String
)