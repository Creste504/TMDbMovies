package com.example.tmdbmovies

import com.google.firebase.Timestamp

data class SearchHistory(
    val query: String = "",
    val timestamp: Timestamp = Timestamp.now()
)