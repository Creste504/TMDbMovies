package com.example.tmdbmovies

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tmdbmovies.adapter.MovieAdapter
import com.example.tmdbmovies.api.TMDBService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var movieAdapter: MovieAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. Configura a grade (RecyclerView)
        recyclerView = findViewById(R.id.recycler_movies)
        recyclerView.layoutManager = GridLayoutManager(this, 2) // 2 colunas como no seu site

        // 2. Inicia a busca dos filmes
        fetchMovies()
    }

    private fun fetchMovies() {
        // Configuração do Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(TMDBService::class.java)

        // Coroutine: faz a busca sem travar o seu celular
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Chama a API com sua chave
                val response = service.getPopularMovies()
                val movies = response.results

                // Volta para a tela principal para mostrar os filmes
                withContext(Dispatchers.Main) {
                    movieAdapter = MovieAdapter(movies)
                    recyclerView.adapter = movieAdapter
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "Erro: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}