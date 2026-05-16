package com.example.tmdbmovies

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val btnRegisterBackHome = findViewById<LinearLayout>(R.id.btn_register_back_home)
        val txtGoToLogin = findViewById<TextView>(R.id.txt_go_to_login)

        // Botão de voltar superior (fecha a tela atual e volta pra Home)
        btnRegisterBackHome.setOnClickListener {
            finish()
        }

        // Quando clicar em "Entrar", fecha o cadastro e volta automaticamente para o login que estava aberto atrás
        txtGoToLogin.setOnClickListener {
            finish()
        }
    }
}