package com.example.tmdbmovies.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tmdbmovies.MovieRating
import com.example.tmdbmovies.SearchHistory
import com.example.tmdbmovies.R // IMPORTANTE: Corrige o erro "Unresolved reference 'R'"

// 1. ADAPTER PARA AS 5 BUSCAS RECENTES (Balãozinho)
class HistoryAdapter(private val list: List<SearchHistory>) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    class HistoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtQuery: TextView = view.findViewById(R.id.txt_history_query)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_search_history, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val history = list[position]
        holder.txtQuery.text = history.query
    }

    override fun getItemCount() = list.size
}

// 2. ADAPTER PARA AS AVALIAÇÕES (Pôster com Opacidade e Estrelas)
class ProfileRatingAdapter(private val list: List<MovieRating>) : RecyclerView.Adapter<ProfileRatingAdapter.RatingViewHolder>() {

    class RatingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgPoster: ImageView = view.findViewById(R.id.img_rating_poster)
        val txtTitle: TextView = view.findViewById(R.id.txt_rating_movie_title)
        val ratingBar: RatingBar = view.findViewById(R.id.profile_item_rating_bar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RatingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_profile_rating, parent, false)
        return RatingViewHolder(view)
    }

    override fun onBindViewHolder(holder: RatingViewHolder, position: Int) {
        val rating = list[position]

        holder.txtTitle.text = rating.movieTitle
        holder.ratingBar.rating = rating.ratingGiven

        // Carrega o pôster de fundo com a imagem do TMDB
        if (!rating.posterPath.isNullOrEmpty()) {
            Glide.with(holder.itemView.context)
                .load("https://image.tmdb.org/t/p/w500${rating.posterPath}")
                .into(holder.imgPoster)
        }
    }

    override fun getItemCount() = list.size
}