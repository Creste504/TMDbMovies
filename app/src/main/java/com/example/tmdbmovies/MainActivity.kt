package com.example.tmdbmovies

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tmdbmovies.adapter.Category
import com.example.tmdbmovies.adapter.MainAdapter
import com.example.tmdbmovies.api.TMDBService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    private val service by lazy { retrofit.create(TMDBService::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recycler_movies)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val searchView = findViewById<SearchView>(R.id.search_view)

        // Configuração da Busca
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    searchMovies(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    fetchMovies() // Se apagar a busca, volta aos populares
                }
                return true
            }
        })

        // Carregamento inicial
        fetchMovies()
    }

    private fun fetchMovies() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = service.getPopularMovies()
                val movies = response.results

                withContext(Dispatchers.Main) {
                    val categories = listOf(
                        Category("Em Alta", movies.shuffled()),
                        Category("Lançamentos", movies.reversed()),
                        Category("Melhores Avaliados", movies)
                    )
                    recyclerView.adapter = MainAdapter(categories)
                }
            } catch (e: Exception) {
                showError(e.message)
            }
        }
    }

    private fun searchMovies(query: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = service.searchMovies(query = query)
                val movies = response.results

                withContext(Dispatchers.Main) {
                    // Mostra o resultado da busca em uma fileira especial
                    val categories = listOf(
                        Category("Resultados para: $query", movies)
                    )
                    recyclerView.adapter = MainAdapter(categories)
                }
            } catch (e: Exception) {
                showError(e.message)
            }
        }
    }

    private suspend fun showError(message: String?) {
        withContext(Dispatchers.Main) {
            Toast.makeText(this@MainActivity, "Erro: $message", Toast.LENGTH_LONG).show()
        }
    }
}