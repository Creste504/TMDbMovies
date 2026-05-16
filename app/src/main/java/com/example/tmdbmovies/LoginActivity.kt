package com.example.tmdbmovies

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val btnBackHome = findViewById<LinearLayout>(R.id.btn_back_home)
        val txtGoToRegister = findViewById<TextView>(R.id.txt_go_to_register)

        btnBackHome.setOnClickListener {
            finish()
        }

        // Quando clicar em "Cadastre-se", abre a tela de cadastro
        txtGoToRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}