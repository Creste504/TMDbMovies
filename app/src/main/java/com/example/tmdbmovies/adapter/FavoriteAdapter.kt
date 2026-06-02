package com.example.tmdbmovies.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tmdbmovies.DetailsActivity
import com.example.tmdbmovies.MovieFavorite
import com.example.tmdbmovies.R

class FavoriteAdapter(private val list: List<MovieFavorite>) : RecyclerView.Adapter<FavoriteAdapter.FavViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_movie, parent, false)
        return FavViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavViewHolder, position: Int) {
        val movie = list[position]
        holder.txtTitle.text = movie.title

        Glide.with(holder.itemView.context)
            .load("https://image.tmdb.org/t/p/w500${movie.posterPath}")
            .into(holder.imgPoster)

        holder.itemView.setOnClickListener {
            val intent = Intent(it.context, DetailsActivity::class.java).apply {
                putExtra("id", movie.id)
                putExtra("title", movie.title)
                putExtra("overview", movie.overview)
                putExtra("poster", movie.posterPath)
                putExtra("rating", movie.rating)
                putExtra("release_date", movie.releaseDate)
            }
            it.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    // CORRIGIDO: Agora usando exatamente R.id.img_poster e R.id.txt_title como no seu MovieAdapter
    class FavViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgPoster: ImageView = view.findViewById(R.id.img_poster)
        val txtTitle: TextView = view.findViewById(R.id.txt_title)
    }
}