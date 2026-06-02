package com.example.tmdbmovies

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tmdbmovies.adapter.CastAdapter
import com.example.tmdbmovies.adapter.CommentAdapter
import com.example.tmdbmovies.api.TMDBService
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DetailsActivity : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private var currentMovieId: Int = 0
    private var isFavorite = false

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    private val service by lazy { retrofit.create(TMDBService::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        val imgBackdrop = findViewById<ImageView>(R.id.img_detail_backdrop)
        val imgPosterSmall = findViewById<ImageView>(R.id.img_detail_poster_small)
        val txtTitle = findViewById<TextView>(R.id.txt_detail_title)
        val txtOverview = findViewById<TextView>(R.id.txt_detail_overview)
        val txtRatingBadge = findViewById<TextView>(R.id.txt_detail_rating)
        val ratingBar = findViewById<RatingBar>(R.id.rating_bar_user)
        val btnFavorite = findViewById<ImageView>(R.id.btn_detail_favorite)
        val txtAno = findViewById<TextView>(R.id.txt_detail_year)
        val txtDuracao = findViewById<TextView>(R.id.txt_detail_duration)
        val txtGeneros = findViewById<TextView>(R.id.txt_detail_genres)
        val rvCast = findViewById<RecyclerView>(R.id.rv_detail_cast)

        // COMPONENTES DE COMENTÁRIOS (Do seu novo card do XML)
        val edtComment = findViewById<EditText>(R.id.edt_detail_comment) // Garanta esse ID no seu EditText do XML
        val btnSendComment = findViewById<AppCompatButton>(R.id.btn_send_comment) // Garanta esse ID no seu Botão do XML
        val rvComments = findViewById<RecyclerView>(R.id.rv_detail_comments) // Garanta esse ID no seu RecyclerView interno de comentários
        val txtNoCommentsEmpty = findViewById<TextView>(R.id.txt_no_comments_empty) // Garanta esse ID no texto de "Nenhum comentário ainda" do XML

        rvCast?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvComments?.layoutManager = LinearLayoutManager(this)

        currentMovieId = intent.getIntExtra("id", -1)
        val title = intent.getStringExtra("title")
        val overview = intent.getStringExtra("overview")
        val posterPath = intent.getStringExtra("poster")
        val rating = intent.getDoubleExtra("rating", 0.0)
        val releaseDate = intent.getStringExtra("release_date")

        txtTitle?.text = title
        txtOverview?.text = overview
        txtRatingBadge?.text = String.format("⭐ %.1f", rating)

        val ano = if (!releaseDate.isNullOrEmpty() && releaseDate.length >= 4) {
            releaseDate.substring(0, 4)
        } else {
            "----"
        }
        txtAno?.text = ano

        if (imgBackdrop != null && !posterPath.isNullOrEmpty()) {
            Glide.with(this).load("https://image.tmdb.org/t/p/w780$posterPath").into(imgBackdrop)
        }
        if (imgPosterSmall != null && !posterPath.isNullOrEmpty()) {
            Glide.with(this).load("https://image.tmdb.org/t/p/w500$posterPath").into(imgPosterSmall)
        }

        if (currentMovieId != -1) {
            checkIfMovieIsFavoriteInFirebase(btnFavorite)
            loadUserRatingFromFirebase(ratingBar)
            // LIGAÇÃO: Monitora e carrega os comentários desse filme em tempo real
            listenToCommentsInFirebase(rvComments, txtNoCommentsEmpty)
        }

        ratingBar?.setOnRatingBarChangeListener { _, ratingValue, fromUser ->
            if (fromUser) {
                saveRatingToFirebase(ratingValue)
            }
        }

        // AÇÃO: Envia o comentário para a nuvem ao clicar no botão amarelo
        btnSendComment?.setOnClickListener {
            val textComment = edtComment?.text?.toString()?.trim() ?: ""
            if (textComment.isNotEmpty()) {
                saveCommentToFirebase(textComment)
                edtComment?.setText("") // Limpa a caixa de texto
            } else {
                Toast.makeText(this, "Escreva um comentário antes de enviar!", Toast.LENGTH_SHORT).show()
            }
        }

        btnFavorite?.setOnClickListener {
            val userId = auth.currentUser?.uid
            if (userId == null) {
                Toast.makeText(this, "Usuário não conectado!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val favoriteRef = firestore.collection("usuarios")
                .document(userId)
                .collection("favoritos")
                .document(currentMovieId.toString())

            if (isFavorite) {
                favoriteRef.delete().addOnSuccessListener {
                    isFavorite = false
                    btnFavorite.setImageResource(android.R.drawable.btn_star_big_off)
                    Toast.makeText(this, "Removido dos favoritos", Toast.LENGTH_SHORT).show()
                }
            } else {
                val movieToSave = MovieFavorite(
                    id = currentMovieId,
                    title = title ?: "Sem título",
                    posterPath = posterPath,
                    overview = overview ?: "",
                    rating = rating,
                    releaseDate = releaseDate
                )

                favoriteRef.set(movieToSave).addOnSuccessListener {
                    isFavorite = true
                    btnFavorite.setImageResource(android.R.drawable.btn_star_big_on)
                    Toast.makeText(this, "Adicionado aos favoritos!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        if (currentMovieId != -1) {
            carregarDadosExtrasDoFilme(currentMovieId, txtDuracao, txtGeneros, rvCast)
        }
    }

    private fun checkIfMovieIsFavoriteInFirebase(btnFavorite: ImageView?) {
        val userId = auth.currentUser?.uid ?: return

        firestore.collection("usuarios")
            .document(userId)
            .collection("favoritos")
            .document(currentMovieId.toString())
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    isFavorite = true
                    btnFavorite?.setImageResource(android.R.drawable.btn_star_big_on)
                } else {
                    isFavorite = false
                    btnFavorite?.setImageResource(android.R.drawable.btn_star_big_off)
                }
            }
    }

    private fun saveRatingToFirebase(ratingValue: Float) {
        val userId = auth.currentUser?.uid ?: return
        val title = intent.getStringExtra("title") ?: "Sem título"
        val posterPath = intent.getStringExtra("poster") ?: ""

        val ratingData = MovieRating(
            movieId = currentMovieId,
            ratingGiven = ratingValue,
            movieTitle = title,
            posterPath = posterPath
        )

        firestore.collection("usuarios")
            .document(userId)
            .collection("avaliacoes")
            .document(currentMovieId.toString())
            .set(ratingData)
            .addOnSuccessListener {
                Toast.makeText(this, "Avaliação salva: $ratingValue ⭐", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadUserRatingFromFirebase(ratingBar: RatingBar?) {
        val userId = auth.currentUser?.uid ?: return

        firestore.collection("usuarios")
            .document(userId)
            .collection("avaliacoes")
            .document(currentMovieId.toString())
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val movieRating = document.toObject(MovieRating::class.java)
                    movieRating?.let {
                        ratingBar?.rating = it.ratingGiven
                    }
                }
            }
    }

    // NOVO: Salva o comentário na coleção global do filme estruturado no Firestore
    private fun saveCommentToFirebase(textComment: String) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "Você precisa estar logado para comentar!", Toast.LENGTH_SHORT).show()
            return
        }

        val commentRef = firestore.collection("comentarios_filmes")
            .document(currentMovieId.toString())
            .collection("comentarios_usuarios")
            .document() // Cria um ID de documento aleatório exclusivo

        val newComment = MovieComment(
            commentId = commentRef.id,
            userId = currentUser.uid,
            username = currentUser.displayName ?: "Usuário Anônimo",
            text = textComment,
            timestamp = Timestamp.now()
        )

        commentRef.set(newComment)
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao enviar comentário.", Toast.LENGTH_SHORT).show()
            }
    }

    // NOVO: Escuta as atualizações da coleção de comentários e atualiza o RecyclerView na hora
    private fun listenToCommentsInFirebase(recyclerView: RecyclerView?, txtEmpty: TextView?) {
        firestore.collection("comentarios_filmes")
            .document(currentMovieId.toString())
            .collection("comentarios_usuarios")
            .orderBy("timestamp", Query.Direction.DESCENDING) // Mais recentes no topo
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener

                val commentsList = snapshot.toObjects(MovieComment::class.java)
                recyclerView?.adapter = CommentAdapter(commentsList)

                if (commentsList.isEmpty()) {
                    txtEmpty?.visibility = View.VISIBLE
                    recyclerView?.visibility = View.GONE
                } else {
                    txtEmpty?.visibility = View.GONE
                    recyclerView?.visibility = View.VISIBLE
                }
            }
    }

    private fun carregarDadosExtrasDoFilme(
        movieId: Int,
        txtDuracao: TextView?,
        txtGeneros: TextView?,
        rvCast: RecyclerView?
    ) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val detalhes = service.getMovieDetails(movieId)
                val creditos = service.getMovieCredits(movieId)

                withContext(Dispatchers.Main) {
                    val minutosTotais = detalhes.runtime ?: 0
                    val horas = minutosTotais / 60
                    val minutosRestantes = minutosTotais % 60
                    txtDuracao?.text = if (horas > 0) "${horas}h ${minutosRestantes}m" else "${minutosRestantes}m"

                    val nomesGeneros = detalhes.genres.map { it.name }.joinToString(", ")
                    txtGeneros?.text = nomesGeneros

                    val principaisAtores = creditos.cast.take(10)
                    rvCast?.adapter = CastAdapter(principaisAtores)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}