package com.example.tmdbmovies

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.tmdbmovies.adapter.ProfileTabAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth

class ProfileActivity : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val txtAvatar = findViewById<TextView>(R.id.txt_profile_avatar)
        val txtUsername = findViewById<TextView>(R.id.txt_profile_username)
        val txtEmail = findViewById<TextView>(R.id.txt_profile_email)

        val tabLayout = findViewById<TabLayout>(R.id.profile_tab_layout)
        val viewPager = findViewById<ViewPager2>(R.id.profile_view_pager)

        val currentUser = auth.currentUser
        if (currentUser != null) {
            val name = currentUser.displayName ?: "Usuário"
            txtUsername.text = name
            txtEmail.text = currentUser.email ?: ""
            txtAvatar.text = name.firstOrNull()?.toString()?.uppercase() ?: "U"

            // Configura o sistema deslizante de abas
            viewPager.adapter = ProfileTabAdapter(this)

            // Vincula os títulos das abas lado a lado
            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                tab.text = when (position) {
                    0 -> "Buscas Recentes"
                    else -> "Minhas Notas"
                }
            }.attach()

        } else {
            Toast.makeText(this, "Usuário deslogado!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}