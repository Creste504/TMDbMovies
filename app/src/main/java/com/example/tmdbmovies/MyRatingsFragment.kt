package com.example.tmdbmovies

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tmdbmovies.R
import com.example.tmdbmovies.MovieRating
import com.example.tmdbmovies.adapter.ProfileRatingAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MyRatingsFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_my_ratings, container, false)
        val rvRatings = view.findViewById<RecyclerView>(R.id.rv_tab_ratings)

        // RESPONSIVIDADE: Identifica se o aparelho está deitado ou em pé
        val orientation = resources.configuration.orientation
        val columns = if (orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE) 4 else 2

        rvRatings.layoutManager = GridLayoutManager(context, columns)

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            FirebaseFirestore.getInstance().collection("usuarios")
                .document(userId).collection("avaliacoes").get()
                .addOnSuccessListener { result ->
                    val list = result.toObjects(MovieRating::class.java)
                    rvRatings.adapter = ProfileRatingAdapter(list)
                }
        }
        return view
    }
}