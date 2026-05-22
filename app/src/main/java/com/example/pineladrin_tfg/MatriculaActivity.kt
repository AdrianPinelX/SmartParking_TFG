package com.example.pineladrin_tfg

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MatriculaActivity : AppCompatActivity() {

    private lateinit var editMatricula: EditText
    private lateinit var txtError: TextView
    private lateinit var btnContinuar: Button

    private var fecha: String? = null
    private var correo: String? = null
    private var plaza: String? = null
    private var tipo: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_matricula)

        val btnVolver = findViewById<Button>(R.id.btnVolver)
        btnVolver.setOnClickListener { finish() }

        editMatricula = findViewById(R.id.editMatricula)
        txtError = findViewById(R.id.txtError)
        btnContinuar = findViewById(R.id.btnContinuar)

        btnContinuar.isEnabled = false

        // 🔥 Recibimos todos los datos
        fecha = intent.getStringExtra("fecha")
        correo = intent.getStringExtra("correo")
        plaza = intent.getStringExtra("plazaSeleccionada")
        tipo = intent.getStringExtra("tipoPlaza")

        editMatricula.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                validarMatricula()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        btnContinuar.setOnClickListener {

            if (!validarMatricula()) {
                txtError.text = "Formato incorrecto. Ejemplos válidos: 1234ABC o A1234BC"
                return@setOnClickListener
            }

            val matricula = editMatricula.text.toString().uppercase()

            val intent = Intent(this, ConfirmacionActivity::class.java)
            intent.putExtra("matricula", matricula)
            intent.putExtra("plazaSeleccionada", plaza)
            intent.putExtra("tipoPlaza", tipo)
            intent.putExtra("correo", correo)
            intent.putExtra("fecha", fecha)
            startActivity(intent)
        }
    }

    private fun validarMatricula(): Boolean {
        val texto = editMatricula.text.toString().uppercase()

        // 🔥 Acepta matrículas modernas y antiguas
        val regex = Regex("(^[0-9]{4}[A-Z]{3}$)|(^[A-Z]{1,2}[0-9]{4}[A-Z]{1,2}$)")

        return if (regex.matches(texto)) {
            txtError.text = ""
            btnContinuar.isEnabled = true
            true
        } else {
            txtError.text = "Formato incorrecto"
            btnContinuar.isEnabled = false
            false
        }
    }
}