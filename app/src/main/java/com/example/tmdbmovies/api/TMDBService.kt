package com.example.tmdbmovies.api

import com.example.tmdbmovies.model.MovieResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface TMDBService {
    // Busca os filmes populares usando sua chave real
    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("api_key") apiKey: String = "92421e75b62598ff5da9a5a99445c11f",
        @Query("language") language: String = "pt-BR"
    ): MovieResponse
    @GET("search/movie")
    suspend fun searchMovies(
        @Query("api_key") apiKey: String = "92421e75b62598ff5da9a5a99445c11f",
        @Query("query") query: String,
        @Query("language") language: String = "pt-BR"
    ): MovieResponse
}