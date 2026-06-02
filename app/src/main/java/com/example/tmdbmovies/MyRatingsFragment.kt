package com.example.tmdbmovies

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tmdbmovies.adapter.ProfileRatingAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MyRatingsFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_my_ratings, container, false)
        val rvRatings = view.findViewById<RecyclerView>(R.id.rv_tab_ratings)
        rvRatings.layoutManager = androidx.recyclerview.widget.GridLayoutManager(context, 2)
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