package com.example.tmdbmovies

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()

        val edtName = findViewById<EditText>(R.id.edt_register_name)
        val edtEmail = findViewById<EditText>(R.id.edt_register_email)
        val edtPassword = findViewById<EditText>(R.id.edt_register_password)
        val btnRegister = findViewById<AppCompatButton>(R.id.btn_register_submit)
        val btnBackHome = findViewById<LinearLayout>(R.id.btn_register_back_home)
        val txtGoToLogin = findViewById<TextView>(R.id.txt_go_to_login)

        btnBackHome.setOnClickListener { finish() }
        txtGoToLogin.setOnClickListener { finish() }

        btnRegister.setOnClickListener {
            val txtName = edtName.text.toString().trim()
            val txtEmail = edtEmail.text.toString().trim()
            val txtPassword = edtPassword.text.toString().trim()

            if (txtName.isEmpty() || txtEmail.isEmpty() || txtPassword.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (txtPassword.length < 6) {
                Toast.makeText(this, "A senha deve ter pelo menos 6 caracteres!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(txtEmail, txtPassword)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(txtName)
                            .build()

                        user?.updateProfile(profileUpdates)?.addOnCompleteListener {
                            Toast.makeText(this, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        }
                    } else {
                        Toast.makeText(this, "Erro ao cadastrar: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }
}