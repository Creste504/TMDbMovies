package com.example.tmdbmovies

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.SearchView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tmdbmovies.adapter.Category
import com.example.tmdbmovies.adapter.MainAdapter
import com.example.tmdbmovies.api.Genre
import com.example.tmdbmovies.api.TMDBService
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var spinnerGenres: Spinner
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var mainAdapter: MainAdapter

    // Lista mutável para conseguirmos adicionar a opção "Todos os gêneros"
    private var genresList: MutableList<Genre> = mutableListOf()

    private val API_KEY = "92421e75b62598ff5da9a5a99445c11f"

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
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        spinnerGenres = findViewById(R.id.spinner_genres)

        mainAdapter = MainAdapter(listOf())
        recyclerView.adapter = mainAdapter

        val searchView = findViewById<SearchView>(R.id.search_view)
        val imgMenu = findViewById<ImageView>(R.id.img_menu)

        // --- CORREÇÃO AQUI: MANTÉM A BARRA ABERTA MAS TRAVA O TECLADO AUTOMÁTICO ---
        searchView.isIconified = false // Mantém a barra sempre aberta visualmente
        searchView.setIconifiedByDefault(false) // Evita que ela feche sozinha
        searchView.clearFocus() // 🔥 Tira o foco inicial para o teclado NÃO abrir sozinho ao entrar na tela

        searchView.setOnClickListener {
            searchView.isIconified = false
            searchView.requestFocus() // Abre o teclado apenas se você clicar de fato na barra
        }

        imgMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        val btnMenuLogin = findViewById<AppCompatButton>(R.id.btn_menu_login)
        val itemPerfil = findViewById<View>(R.id.item_menu_perfil)
        val itemFavoritos = findViewById<View>(R.id.item_menu_favoritos)

        btnMenuLogin?.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            startActivity(Intent(this, LoginActivity::class.java))
        }

        itemPerfil?.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        // CONECTADO: Abre a tela de favoritos integrada com o Firebase Firestore
        itemFavoritos?.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            startActivity(Intent(this, FavoritesActivity::class.java))
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
                    fetchMoviesAndBuildCategories()
                }
                return true
            }
        })

        // --- MUDANÇA: LÓGICA DO SPINNER ADAPTADA PARA "TODOS OS GÊNEROS" ---
        spinnerGenres.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (genresList.isNotEmpty() && position < genresList.size) {
                    val selectedGenre = genresList[position]

                    if (selectedGenre.id == -1) {
                        // Se for o "Todos os gêneros", mostra a lista padrão completa
                        fetchMoviesAndBuildCategories()
                    } else {
                        // Se for um gênero real, filtra por ele
                        fetchMoviesByGenre(selectedGenre)
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        fetchGenres()
        fetchMoviesAndBuildCategories()

        onBackPressedDispatcher.addCallback(this, object : androidx.activity.OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    remove()
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })

        updateMenuUI()
    }

    private fun updateMenuUI() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val layoutDeslogado = findViewById<View>(R.id.layout_menu_deslogado)
        val layoutLogado = findViewById<View>(R.id.layout_menu_logado)
        val txtUsername = findViewById<TextView>(R.id.txt_menu_username)
        val txtAvatar = findViewById<TextView>(R.id.txt_menu_avatar)
        val btnLogout = findViewById<TextView>(R.id.btn_menu_logout)

        if (currentUser != null) {
            layoutDeslogado?.visibility = View.GONE
            layoutLogado?.visibility = View.VISIBLE

            val name = currentUser.displayName ?: "Usuário"
            txtUsername?.text = name
            txtAvatar?.text = name.firstOrNull()?.toString()?.uppercase() ?: "U"

            btnLogout?.setOnClickListener {
                FirebaseAuth.getInstance().signOut()
                updateMenuUI()
                drawerLayout.closeDrawer(GravityCompat.START)
            }
        } else {
            layoutDeslogado?.visibility = View.VISIBLE
            layoutLogado?.visibility = View.GONE
        }
    }

    // --- MUDANÇA: INSERÇÃO MANUAL DE "TODOS OS GÊNEROS" ---
    private fun fetchGenres() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = service.getGenres()

                withContext(Dispatchers.Main) {
                    genresList.clear()
                    // Criamos um gênero "falso" com ID -1 para representar a opção de limpar o filtro
                    genresList.add(Genre(id = -1, name = "Todos os gêneros"))
                    genresList.addAll(response.genres)

                    val genreNames = genresList.map { it.name }
                    val adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_item, genreNames)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerGenres.adapter = adapter
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun fetchMoviesAndBuildCategories() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = service.getPopularMovies()
                val moviesList = response.results

                val categoriesList = listOf(
                    Category("Populares", moviesList),
                    Category("Mais Vistos", moviesList.shuffled()),
                    Category("Lançamentos", moviesList.reversed())
                )

                withContext(Dispatchers.Main) {
                    mainAdapter.updateCategories(categoriesList)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "Erro ao carregar filmes", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun fetchMoviesByGenre(genre: Genre) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = service.getMoviesByGenre(genreId = genre.id.toString())
                val moviesList = response.results

                val genreCategories = listOf(
                    Category("Populares em: ${genre.name}", moviesList),
                    Category("Recomendados", moviesList.shuffled())
                )

                withContext(Dispatchers.Main) {
                    mainAdapter.updateCategories(genreCategories)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun searchMovies(query: String) {
        // MODIFICADO: Salva o termo pesquisado e a data/hora na nuvem vinculado ao ID do usuário ativo
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null && query.isNotEmpty()) {
            val searchData = SearchHistory(query = query, timestamp = com.google.firebase.Timestamp.now())

            // Grava o histórico usando caminhos diretos (usuarios -> UID -> buscas_recentes)
            com.google.firebase.firestore.FirebaseFirestore.getInstance()
                .collection("usuarios")
                .document(userId)
                .collection("buscas_recentes")
                .add(searchData)
        }

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = service.searchMovies(query = query)
                val moviesList = response.results

                val searchCategory = listOf(
                    Category("Resultado da Busca: '$query'", moviesList)
                )

                withContext(Dispatchers.Main) {
                    mainAdapter.updateCategories(searchCategory)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}