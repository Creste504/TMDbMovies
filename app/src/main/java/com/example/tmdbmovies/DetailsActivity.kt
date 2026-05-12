package com.example.tmdbmovies

import android.os.Bundle
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class DetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        // 1. Conecta o Kotlin com os novos IDs do XML
        val imgBackdrop = findViewById<ImageView>(R.id.img_detail_backdrop)
        val imgPosterSmall = findViewById<ImageView>(R.id.img_detail_poster_small)
        val txtTitle = findViewById<TextView>(R.id.txt_detail_title)
        val txtOverview = findViewById<TextView>(R.id.txt_detail_overview)
        val txtRatingBadge = findViewById<TextView>(R.id.txt_detail_rating)
        val ratingBar = findViewById<RatingBar>(R.id.rating_bar_user)

        // 2. Pega os dados que o Adapter enviou
        val title = intent.getStringExtra("title")
        val overview = intent.getStringExtra("overview")
        val posterPath = intent.getStringExtra("poster")
        val rating = intent.getDoubleExtra("rating", 0.0)

        // 3. Preenche os textos
        txtTitle.text = title
        txtOverview.text = overview
        txtRatingBadge.text = String.format("%.1f", rating) // Mostra ex: 8.2

        // 4. Configura as estrelas (TMDb é 0-10, RatingBar é 0-5, então dividimos por 2)
        ratingBar.rating = (rating / 2).toFloat()

        // 5. Carrega as imagens com o Glide
        // Imagem de fundo (Backdrop)
        Glide.with(this)
            .load("https://image.tmdb.org/t/p/w780$posterPath")
            .into(imgBackdrop)

        // Poster pequeno (aquele que fica por cima)
        Glide.with(this)
            .load("https://image.tmdb.org/t/p/w500$posterPath")
            .into(imgPosterSmall)
    }
}