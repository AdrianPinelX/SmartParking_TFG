package com.example.pineladrin_tfg

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class PlanoActivity : AppCompatActivity() {

    private lateinit var spinnerPlantas: Spinner
    private lateinit var spinnerPlazas: Spinner
    private lateinit var imagePlano: ImageView
    private lateinit var btnContinuar: Button

    private var tipoPlaza: String = "Sencilla"
    private var plazaSeleccionada: String? = null
    private var fecha: String? = null
    private var correo: String? = null

    private val db = FirebaseFirestore.getInstance()
    private var plazasOcupadas = mutableListOf<String>()

    // 🔴 Plazas que NUNCA se pueden usar (rojas en el plano)
    private val plazasProhibidas = listOf(
        "A6", "A11", "A12",      // Planta 1
        "B8", "B9", "B13", "B14",// Planta 2
        "C14", "C15"             // Planta 3
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plano)

        val btnVolver = findViewById<Button>(R.id.btnVolver)
        btnVolver.setOnClickListener { finish() }

        spinnerPlantas = findViewById(R.id.spinnerPlantas)
        spinnerPlazas = findViewById(R.id.spinnerPlazas)
        imagePlano = findViewById(R.id.imagePlano)
        btnContinuar = findViewById(R.id.btnContinuar)

        tipoPlaza = intent.getStringExtra("tipoPlaza") ?: "Sencilla"
        fecha = intent.getStringExtra("fecha")
        correo = intent.getStringExtra("correo")

        btnContinuar.isEnabled = false

        val plantas = listOf("Planta 1", "Planta 2", "Planta 3")
        val adapterPlantas = ArrayAdapter(this, android.R.layout.simple_spinner_item, plantas)
        adapterPlantas.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerPlantas.adapter = adapterPlantas

        // Cargamos plazas ocupadas para esa fecha
        cargarPlazasOcupadas()

        spinnerPlantas.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                cargarPlano(position + 1)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        btnContinuar.setOnClickListener {
            val intent = Intent(this, MatriculaActivity::class.java)
            intent.putExtra("plazaSeleccionada", plazaSeleccionada)
            intent.putExtra("tipoPlaza", tipoPlaza)
            intent.putExtra("correo", correo)
            intent.putExtra("fecha", fecha)
            startActivity(intent)
        }
    }

    private fun cargarPlazasOcupadas() {
        if (fecha == null) return

        db.collection("reservas")
            .whereEqualTo("fecha", fecha)
            .get()
            .addOnSuccessListener { docs ->
                plazasOcupadas.clear()
                for (doc in docs) {
                    val plaza = doc.getString("plaza")
                    if (plaza != null) plazasOcupadas.add(plaza)
                }
                // Una vez cargadas, mostramos por defecto la planta 1
                cargarPlano(1)
            }
    }

    private fun cargarPlano(planta: Int) {
        val imagen = when (planta) {
            1 -> R.drawable.planta1
            2 -> R.drawable.planta2
            3 -> R.drawable.planta3
            else -> R.drawable.planta1
        }

        imagePlano.setImageResource(imagen)

        val plazas = obtenerPlazas(planta)

        // 🔥 Solo plazas:
        // - del tipo correcto
        // - que NO estén ocupadas ese día
        // - que NO sean de las prohibidas (rojas)
        val plazasDisponibles = plazas.filter {
            puedeSeleccionar(it.tipo)
                    && !plazasOcupadas.contains(it.nombre)
                    && !plazasProhibidas.contains(it.nombre)
        }.map { it.nombre }

        val adapterPlazas = ArrayAdapter(this, android.R.layout.simple_spinner_item, plazasDisponibles)
        adapterPlazas.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerPlazas.adapter = adapterPlazas

        spinnerPlazas.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                plazaSeleccionada = plazasDisponibles[position]
                btnContinuar.isEnabled = true
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun puedeSeleccionar(tipo: String): Boolean {
        return when (tipoPlaza) {
            "Sencilla" -> tipo == "sencilla"
            "Discapacitado" -> tipo == "discapacitado"
            "Eléctrico" -> tipo == "electrico"
            else -> false
        }
    }

    data class Plaza(val nombre: String, val tipo: String)

    private fun obtenerPlazas(planta: Int): List<Plaza> {
        return when (planta) {
            1 -> listOf(
                Plaza("A1", "sencilla"), Plaza("A2", "sencilla"),
                Plaza("A3", "discapacitado"), Plaza("A4", "discapacitado"),
                Plaza("A5", "sencilla"), Plaza("A6", "sencilla"),
                Plaza("A7", "sencilla"), Plaza("A8", "sencilla"),
                Plaza("A9", "electrico"), Plaza("A10", "electrico"),
                Plaza("A11", "sencilla"), Plaza("A12", "sencilla")
            )

            2 -> listOf(
                Plaza("B1", "discapacitado"), Plaza("B2", "electrico"),
                Plaza("B3", "electrico"), Plaza("B4", "sencilla"),
                Plaza("B5", "sencilla"), Plaza("B6", "sencilla"),
                Plaza("B7", "sencilla"), Plaza("B8", "sencilla"),
                Plaza("B9", "sencilla"), Plaza("B10", "sencilla"),
                Plaza("B11", "sencilla"), Plaza("B12", "sencilla"),
                Plaza("B13", "sencilla"), Plaza("B14", "sencilla")
            )

            3 -> listOf(
                Plaza("C1", "sencilla"), Plaza("C2", "sencilla"),
                Plaza("C3", "electrico"), Plaza("C4", "electrico"),
                Plaza("C5", "sencilla"), Plaza("C6", "sencilla"),
                Plaza("C7", "sencilla"), Plaza("C8", "sencilla"),
                Plaza("C9", "discapacitado"), Plaza("C10", "discapacitado"),
                Plaza("C11", "sencilla"), Plaza("C12", "sencilla"),
                Plaza("C13", "sencilla"), Plaza("C14", "sencilla"),
                Plaza("C15", "sencilla")
            )

            else -> emptyList()
        }
    }
}