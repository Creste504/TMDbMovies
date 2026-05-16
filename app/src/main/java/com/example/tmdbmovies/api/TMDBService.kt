package com.example.tmdbmovies.api // Garanta que o seu pacote esteja correto aqui em cima

import com.example.tmdbmovies.model.MovieResponse
import retrofit2.http.GET
import retrofit2.http.Query

// 1. AS CLASSES DE RESPOSTA FICAM AQUI (FORA DA INTERFACE)
data class GenreResponse(val genres: List<Genre>)
data class Genre(val id: Int, val name: String)


// 2. A SUA INTERFACE ADICIONA AS NOVAS FUNÇÕES SEM APAGAR AS ANTIGAS
interface TMDBService {

    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("api_key") apiKey: String = "92421e75b62598ff5da9a5a99445c11f",
        @Query("language") language: String = "pt-BR"
    ): MovieResponse

    // --- COLE AS NOVAS FUNÇÕES DAQUI PARA BAIXO ---

    @GET("search/movie")
    suspend fun searchMovies(
        @Query("api_key") apiKey: String = "92421e75b62598ff5da9a5a99445c11f",
        @Query("query") query: String,
        @Query("language") language: String = "pt-BR"
    ): MovieResponse

    @GET("genre/movie/list")
    suspend fun getGenres(
        @Query("api_key") apiKey: String = "92421e75b62598ff5da9a5a99445c11f",
        @Query("language") language: String = "pt-BR"
    ): GenreResponse

    @GET("discover/movie")
    suspend fun getMoviesByGenre(
        @Query("api_key") apiKey: String = "92421e75b62598ff5da9a5a99445c11f",
        @Query("with_genres") genreId: String,
        @Query("language") language: String = "pt-BR"
    ): MovieResponse
}