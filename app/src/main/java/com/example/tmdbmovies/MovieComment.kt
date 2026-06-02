package com.example.tmdbmovies

import com.google.firebase.Timestamp

data class MovieComment(
    val commentId: String = "",
    val userId: String = "",
    val username: String = "",
    val text: String = "",
    val timestamp: Timestamp = Timestamp.now()
)