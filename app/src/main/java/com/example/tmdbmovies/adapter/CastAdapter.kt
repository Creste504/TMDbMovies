package com.example.tmdbmovies.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tmdbmovies.R
import com.example.tmdbmovies.model.CastMember

class CastAdapter(private val castList: List<CastMember>) : RecyclerView.Adapter<CastAdapter.CastViewHolder>() {

    class CastViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgProfile: ImageView = view.findViewById(R.id.img_cast_profile)
        val txtName: TextView = view.findViewById(R.id.txt_cast_name)
        val txtCharacter: TextView = view.findViewById(R.id.txt_cast_character)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CastViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cast, parent, false)
        return CastViewHolder(view)
    }

    override fun onBindViewHolder(holder: CastViewHolder, position: Int) {
        val member = castList[position]
        holder.txtName.text = member.name
        holder.txtCharacter.text = member.character

        val imageUrl = "https://image.tmdb.org/t/p/w185${member.profilePath}"
        Glide.with(holder.itemView.context)
            .load(imageUrl)
            .placeholder(android.R.drawable.progress_horizontal)
            .error(android.R.drawable.sym_def_app_icon)
            .into(holder.imgProfile)
    }

    override fun getItemCount(): Int = castList.size
}