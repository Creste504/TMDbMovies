package com.example.tmdbmovies.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tmdbmovies.R // Importante para reconhecer o layout
import com.example.tmdbmovies.model.Movie // Importante para reconhecer o molde do filme
import com.example.tmdbmovies.DetailsActivity

// Esta CLASSE liga seus dados à interface
class MovieAdapter(private val movies: List<Movie>) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    class MovieViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgPoster: ImageView = view.findViewById(R.id.img_poster)
        val txtTitle: TextView = view.findViewById(R.id.txt_title)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        // Aqui ele busca o arquivo item_movie.xml que você criou
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_movie, parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = movies[position]
        holder.txtTitle.text = movie.title

        val imageUrl = "https://image.tmdb.org/t/p/w500${movie.posterPath}"
        Glide.with(holder.itemView.context).load(imageUrl).into(holder.imgPoster)

        // --- ADICIONE ESTA PARTE AQUI EMBAIXO ---
        holder.itemView.setOnClickListener {
            val intent = android.content.Intent(holder.itemView.context, DetailsActivity::class.java)
            intent.putExtra("title", movie.title)
            intent.putExtra("overview", movie.overview)
            intent.putExtra("poster", movie.posterPath)
            intent.putExtra("rating", movie.voteAverage)
            holder.itemView.context.startActivity(intent)
        }
    }
    override fun getItemCount(): Int = movies.size
}