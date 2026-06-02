package com.example.tmdbmovies.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tmdbmovies.MovieComment
import com.example.tmdbmovies.R

class CommentAdapter(private val list: List<MovieComment>) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    class CommentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtUsername: TextView = view.findViewById(R.id.txt_comment_username)
        val txtText: TextView = view.findViewById(R.id.txt_comment_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_comment, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = list[position]
        holder.txtUsername.text = comment.username
        holder.txtText.text = comment.text
    }

    override fun getItemCount() = list.size
}