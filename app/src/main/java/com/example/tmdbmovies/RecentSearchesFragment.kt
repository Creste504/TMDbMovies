package com.example.tmdbmovies

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tmdbmovies.adapter.HistoryAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class RecentSearchesFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_recent_searches, container, false)
        val rvSearches = view.findViewById<RecyclerView>(R.id.rv_tab_searches)
        rvSearches.layoutManager = LinearLayoutManager(context)

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            FirebaseFirestore.getInstance().collection("usuarios")
                .document(userId).collection("buscas_recentes")
                .orderBy("timestamp", Query.Direction.DESCENDING).limit(5).get()
                .addOnSuccessListener { result ->
                    val list = result.toObjects(SearchHistory::class.java)
                    rvSearches.adapter = HistoryAdapter(list)
                }
        }
        return view
    }
}