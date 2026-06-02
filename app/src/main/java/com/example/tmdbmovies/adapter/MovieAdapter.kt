package com.example.tmdbmovies.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tmdbmovies.DetailsActivity
import com.example.tmdbmovies.R
import com.example.tmdbmovies.model.Movie

// Representa cada linha da tela inicial (ex: Populares, Lançamentos)
data class Category(val title: String, var movies: List<Movie>)

// 1. ADAPTER PRINCIPAL (Controla as linhas verticais)
class MainAdapter(private var categories: List<Category>) : RecyclerView.Adapter<MainAdapter.MainViewHolder>() {

    class MainViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtTitle = view.findViewById<TextView>(R.id.txt_row_title)
        val rvHorizontal = view.findViewById<RecyclerView>(R.id.rv_movies_horizontal)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_row, parent, false)
        return MainViewHolder(view)
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val category = categories[position]
        holder.txtTitle.text = category.title

        // Configura a lista horizontal interna
        holder.rvHorizontal.layoutManager = LinearLayoutManager(holder.itemView.context, LinearLayoutManager.HORIZONTAL, false)
        holder.rvHorizontal.adapter = MovieAdapter(category.movies)
    }

    override fun getItemCount() = categories.size

    // Função para atualizar as categorias na MainActivity
    fun updateCategories(newCategories: List<Category>) {
        this.categories = newCategories
        notifyDataSetChanged()
    }
}

// 2. ADAPTER SECUNDÁRIO (Controla cada filme individual dentro da linha horizontal)
class MovieAdapter : RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private val movies: List<Movie>

    constructor(movies: List<Movie>) : super() {
        this.movies = movies
    }

    class MovieViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgPoster: ImageView = view.findViewById(R.id.img_poster)
        val txtTitle: TextView = view.findViewById(R.id.txt_title)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_movie, parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = movies[position]
        holder.txtTitle.text = movie.title

        // Carrega a imagem no pôster
        val imageUrl = "https://image.tmdb.org/t/p/w500${movie.posterPath}"
        Glide.with(holder.itemView.context).load(imageUrl).into(holder.imgPoster)

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, DetailsActivity::class.java)
            intent.putExtra("id", movie.id)
            intent.putExtra("title", movie.title)
            intent.putExtra("overview", movie.overview)
            intent.putExtra("poster", movie.posterPath)
            intent.putExtra("rating", movie.voteAverage)

            // CORREÇÃO: Mudado de releaseDate para release_date (ou o nome exato que está no seu Movie.kt)
            intent.putExtra("release_date", movie.releaseDate)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = movies.size
}