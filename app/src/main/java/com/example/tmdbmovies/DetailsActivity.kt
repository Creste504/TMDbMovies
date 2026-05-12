package com.example.tmdbmovies

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class DetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        val imgBackdrop = findViewById<ImageView>(R.id.img_detail_poster)
        val txtTitle = findViewById<TextView>(R.id.txt_detail_title)
        val txtOverview = findViewById<TextView>(R.id.txt_detail_overview)
        val txtRating = findViewById<TextView>(R.id.txt_detail_rating)

        // Recebe os dados vindos do Adapter
        val title = intent.getStringExtra("title")
        val overview = intent.getStringExtra("overview")
        val poster = intent.getStringExtra("poster")
        val rating = intent.getDoubleExtra("rating", 0.0)

        // Preenche a tela
        txtTitle.text = title
        txtOverview.text = overview
        txtRating.text = "Avaliação: ★ $rating"

        Glide.with(this)
            .load("https://image.tmdb.org/t/p/w780$poster")
            .into(imgBackdrop)
    }
}