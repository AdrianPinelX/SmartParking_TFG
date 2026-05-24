package com.example.pineladrin_tfg

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar
import java.util.Locale

class FormularioActivity : AppCompatActivity() {

    private lateinit var radioDiscapacitado: RadioButton
    private lateinit var radioNormal: RadioButton
    private lateinit var radioEléctrico: RadioButton
    private lateinit var btnContinuar: Button

    private var tipoSeleccionado = ""
    private var fechaSeleccionada: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_formulario)

        val btnVolver = findViewById<Button>(R.id.btnVolver)
        btnVolver.setOnClickListener { finish() }

        val btnSeleccionarFecha = findViewById<Button>(R.id.btnSeleccionarFecha)
        val txtFechaSeleccionada = findViewById<TextView>(R.id.txtFechaSeleccionada)

        btnContinuar = findViewById(R.id.btnContinuar)
        btnContinuar.isEnabled = false

        val correo = intent.getStringExtra("correo") ?: ""

        btnSeleccionarFecha.setOnClickListener {

            Toast.makeText(
                this,
                "El garaje estará abierto de 7:00 a 22:00",
                Toast.LENGTH_LONG
            ).show()

            Locale.setDefault(Locale("es", "ES"))

            // Fecha actual del sistema
            val hoy = Calendar.getInstance()
            val yearHoy = hoy.get(Calendar.YEAR)
            val mesHoy = hoy.get(Calendar.MONTH)
            val diaHoy = hoy.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->

                    // Guardar fecha seleccionada
                    fechaSeleccionada = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                    txtFechaSeleccionada.text = fechaSeleccionada
                    validarFormulario()
                },
                yearHoy, mesHoy, diaHoy
            )

            //  BLOQUEAR FECHAS PASADAS
            datePicker.datePicker.minDate = hoy.timeInMillis

            datePicker.show()
        }

        radioDiscapacitado = findViewById(R.id.radioDiscapacitado)
        radioNormal = findViewById(R.id.radioNormal)
        radioEléctrico = findViewById(R.id.radioElectrico)

        radioDiscapacitado.setOnCheckedChangeListener { _, checked ->
            if (checked) {
                radioNormal.isChecked = false
                radioEléctrico.isChecked = false
                tipoSeleccionado = "Discapacitado"
                validarFormulario()
            }
        }

        radioNormal.setOnCheckedChangeListener { _, checked ->
            if (checked) {
                radioDiscapacitado.isChecked = false
                radioEléctrico.isChecked = false
                tipoSeleccionado = "Sencilla"
                validarFormulario()
            }
        }

        radioEléctrico.setOnCheckedChangeListener { _, checked ->
            if (checked) {
                radioDiscapacitado.isChecked = false
                radioNormal.isChecked = false
                tipoSeleccionado = "Eléctrico"
                validarFormulario()
            }
        }

        btnContinuar.setOnClickListener {
            val intent = Intent(this, PlanoActivity::class.java)
            intent.putExtra("tipoPlaza", tipoSeleccionado)
            intent.putExtra("correo", correo)
            intent.putExtra("fecha", fechaSeleccionada)
            startActivity(intent)
        }
    }

    private fun validarFormulario() {
        btnContinuar.isEnabled =
            fechaSeleccionada != null && tipoSeleccionado.isNotEmpty()
    }
}