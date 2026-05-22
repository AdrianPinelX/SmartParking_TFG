package com.example.pineladrin_tfg

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class MisReservasActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ReservaAdapter
    private val listaReservas = mutableListOf<Reserva>()
    private val db = FirebaseFirestore.getInstance()

    private var correoUsuario: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mis_reservas)

        correoUsuario = intent.getStringExtra("correo") ?: ""

        val btnVolver = findViewById<Button>(R.id.btnVolver)
        btnVolver.setOnClickListener { finish() }

        recyclerView = findViewById(R.id.recyclerReservas)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = ReservaAdapter(
            listaReservas,
            onClick = { reserva ->
                val intent = Intent(this, DetalleReservaActivity::class.java)
                intent.putExtra("idReserva", reserva.id)
                startActivity(intent)
            },
            onDelete = { reserva ->
                eliminarReserva(reserva.id)
            }
        )

        recyclerView.adapter = adapter

        cargarReservas()
    }

    override fun onResume() {
        super.onResume()
        cargarReservas()   // Recarga la lista al volver
    }

    private fun cargarReservas() {

        if (correoUsuario.isEmpty()) {
            Toast.makeText(this, "Error: correo no recibido", Toast.LENGTH_SHORT).show()
            return
        }

        db.collection("reservas")
            .whereEqualTo("correo", correoUsuario)
            .get()
            .addOnSuccessListener { docs ->
                listaReservas.clear()

                if (docs.isEmpty) {
                    adapter.notifyDataSetChanged()
                    return@addOnSuccessListener
                }

                val temp = mutableListOf<Pair<Reserva, Long>>()

                for (doc in docs) {
                    val reserva = Reserva(
                        id = doc.id,
                        fecha = doc.getString("fecha") ?: "",
                        plaza = doc.getString("plaza") ?: "",
                        matricula = doc.getString("matricula") ?: ""
                    )
                    val ts = doc.getLong("timestamp") ?: 0L
                    temp.add(reserva to ts)
                }

                temp.sortByDescending { it.second }
                temp.forEach { pair ->
                    listaReservas.add(pair.first)
                }

                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al cargar reservas", Toast.LENGTH_SHORT).show()
            }
    }

    // ============================================================
    //     ELIMINAR RESERVA CON ALERTDIALOG DE CONFIRMACIÓN
    // ============================================================
    private fun eliminarReserva(id: String) {

        AlertDialog.Builder(this)
            .setTitle("Confirmar eliminación")
            .setMessage("¿Estás seguro de que deseas eliminar esta reserva?")
            .setPositiveButton("Sí") { _, _ ->

                db.collection("reservas").document(id)
                    .delete()
                    .addOnSuccessListener {
                        Toast.makeText(this, "Reserva eliminada", Toast.LENGTH_SHORT).show()
                        cargarReservas()  // Recargar lista
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Error al eliminar", Toast.LENGTH_SHORT).show()
                    }
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}