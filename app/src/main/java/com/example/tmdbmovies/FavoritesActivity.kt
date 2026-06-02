package com.example.tmdbmovies

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tmdbmovies.adapter.FavoriteAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FavoritesActivity : AppCompatActivity() {

    // Instâncias do Firebase para conectar com o Login/Cadastro
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Garanta que este nome seja o mesmo do seu arquivo XML atual de favoritos
        setContentView(R.layout.activity_favorites)

        // Encontra o seu RecyclerView no XML
        val rvFavorites = findViewById<RecyclerView>(R.id.rv_favorites)

        // MODIFICADO: Define para exibir os filmes em formato de grade com no máximo 2 por fileira
        rvFavorites.layoutManager = GridLayoutManager(this, 2)

        // Dispara a busca no banco de dados da nuvem
        carregarFavoritos(rvFavorites)
    }

    private fun carregarFavoritos(recyclerView: RecyclerView) {
        val userId = auth.currentUser?.uid ?: return

        // Acessa: usuarios -> SEU_ID -> favoritos
        firestore.collection("usuarios")
            .document(userId)
            .collection("favoritos")
            .get()
            .addOnSuccessListener { result ->
                // Converte os dados do Firebase de volta para objetos do tipo MovieFavorite
                val listFav = result.toObjects(MovieFavorite::class.java)

                // Conecta o adapter novo com a sua lista
                recyclerView.adapter = FavoriteAdapter(listFav)

                if (listFav.isEmpty()) {
                    Toast.makeText(this, "Você não tem favoritos ainda!", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao carregar favoritos", Toast.LENGTH_SHORT).show()
            }
    }
}