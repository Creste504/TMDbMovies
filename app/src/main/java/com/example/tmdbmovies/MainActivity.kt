package com.example.tmdbmovies

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tmdbmovies.adapter.Category
import com.example.tmdbmovies.adapter.MainAdapter
import com.example.tmdbmovies.api.Genre
import com.example.tmdbmovies.api.TMDBService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.annotation.SuppressLint
import android.widget.ImageView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var spinnerGenres: Spinner
    private lateinit var drawerLayout: DrawerLayout
    private var genreList: List<Genre> = listOf()

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
        drawerLayout = findViewById(R.id.drawer_layout)
        recyclerView = findViewById(R.id.recycler_movies)
        recyclerView.layoutManager = LinearLayoutManager(this)
        spinnerGenres = findViewById(R.id.spinner_genres)

        val searchView = findViewById<SearchView>(R.id.search_view)
        val imgMenu = findViewById<ImageView>(R.id.img_menu) // <-- Resolvido com o Alt + Enter!

        imgMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    searchMovies(query)
                }
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    fetchMovies()
                }
                return true
            }

        })

        fetchGenres()
        fetchMovies()

        // Trata o botão "Voltar" de forma segura dentro do onCreate
        onBackPressedDispatcher.addCallback(this, object : androidx.activity.OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Se o menu lateral estiver aberto, apenas fecha ele
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    // Se estiver fechado, remove esse callback e deixa o sistema fechar a activity
                    remove()
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })
        val btnMenuLogin = findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.btn_menu_login)
        btnMenuLogin.setOnClickListener {
            // Fecha o menu lateral antes de ir para a outra tela
            drawerLayout.closeDrawer(GravityCompat.START)

            // Abre a tela de Login
            val intent = android.content.Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    } // <-- O seu onCreate fecha aqui!
    private fun fetchGenres() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = service.getGenres()
                genreList = listOf(Genre(-1, "Todos os Gêneros")) + response.genres

                withContext(Dispatchers.Main) {
                    val genreNames = genreList.map { it.name }
                    val adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_item, genreNames)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerGenres.adapter = adapter

                    spinnerGenres.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                            val selectedGenre = genreList[position]
                            if (selectedGenre.id == -1) {
                                fetchMovies() // Se for "Todos", mostra os populares
                            } else {
                                fetchMoviesByGenre(selectedGenre.id.toString(), selectedGenre.name)
                            }
                        }
                        override fun onNothingSelected(parent: AdapterView<*>?) {}
                    }
                }
            } catch (e: Exception) {
                showError(e.message)
            }
        }
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

    private fun fetchMoviesByGenre(genreId: String, genreName: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = service.getMoviesByGenre(genreId = genreId)
                val movies = response.results
                withContext(Dispatchers.Main) {
                    val categories = listOf(Category("Filmes de $genreName", movies))
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
                    val categories = listOf(Category("Resultados para: $query", movies))
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