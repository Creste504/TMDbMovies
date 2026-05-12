package com.example.tmdbmovies.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tmdbmovies.R
import com.example.tmdbmovies.model.Movie

data class Category(val title: String, val movies: List<Movie>)

class MainAdapter(private val categories: List<Category>) : RecyclerView.Adapter<MainAdapter.MainViewHolder>() {

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
}