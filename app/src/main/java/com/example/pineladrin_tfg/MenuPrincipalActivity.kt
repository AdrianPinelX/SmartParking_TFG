package com.example.pineladrin_tfg

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth

class MenuPrincipalActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_principal)

        auth = FirebaseAuth.getInstance()

        val correo = intent.getStringExtra("correo") ?: ""

        val btnHacerReserva = findViewById<Button>(R.id.btnHacerReserva)
        val btnMisReservas = findViewById<Button>(R.id.btnMisReservas)
        val btnCerrarSesion = findViewById<Button>(R.id.btnCerrarSesion)

        btnHacerReserva.setOnClickListener {
            val intent = Intent(this, FormularioActivity::class.java)
            intent.putExtra("correo", correo)
            startActivity(intent)
        }

        btnMisReservas.setOnClickListener {
            val intent = Intent(this, MisReservasActivity::class.java)
            intent.putExtra("correo", correo)
            startActivity(intent)
        }

        btnCerrarSesion.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}