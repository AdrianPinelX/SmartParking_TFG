package com.example.pineladrin_tfg

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter

class DetalleReservaActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_reserva)

        val btnVolver = findViewById<Button>(R.id.btnVolver)
        val txtFecha = findViewById<TextView>(R.id.txtFecha)
        val txtPlaza = findViewById<TextView>(R.id.txtPlaza)
        val txtMatricula = findViewById<TextView>(R.id.txtMatricula)
        val txtTipo = findViewById<TextView>(R.id.txtTipo)
        val imageQR = findViewById<ImageView>(R.id.imageQR)
        val btnEliminar = findViewById<Button>(R.id.btnEliminar)

        btnVolver.setOnClickListener { finish() }

        val idReserva = intent.getStringExtra("idReserva") ?: ""

        if (idReserva.isEmpty()) {
            Toast.makeText(this, "Error: reserva no encontrada", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // BOTÓN ELIMINAR CON CONFIRMACIÓN
        btnEliminar.setOnClickListener {

            AlertDialog.Builder(this)
                .setTitle("Confirmar eliminación")
                .setMessage("¿Estás seguro de que deseas eliminar esta reserva?")
                .setPositiveButton("Sí") { _, _ ->

                    db.collection("reservas").document(idReserva)
                        .delete()
                        .addOnSuccessListener {
                            Toast.makeText(this, "Reserva eliminada", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Error al eliminar la reserva", Toast.LENGTH_SHORT).show()
                        }
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }

        // Cargar datos de la reserva desde Firestore
        db.collection("reservas").document(idReserva)
            .get()
            .addOnSuccessListener { doc ->
                if (!doc.exists()) {
                    Toast.makeText(this, "La reserva no existe", Toast.LENGTH_SHORT).show()
                    finish()
                    return@addOnSuccessListener
                }

                val fecha = doc.getString("fecha") ?: ""
                val plaza = doc.getString("plaza") ?: ""
                val matricula = doc.getString("matricula") ?: ""
                val tipo = doc.getString("tipo") ?: ""

                txtFecha.text = "Fecha: $fecha"
                txtPlaza.text = "Plaza: $plaza"
                txtMatricula.text = "Matrícula: $matricula"
                txtTipo.text = "Tipo: $tipo"

                // Generar QR con el ID de la reserva
                generarQR(idReserva, imageQR)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al cargar la reserva", Toast.LENGTH_SHORT).show()
            }
    }

    // ============================
    //   GENERAR CÓDIGO QR GRANDE
    // ============================
    private fun generarQR(texto: String, imageView: ImageView) {
        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(texto, BarcodeFormat.QR_CODE, 900, 900)

        val width = bitMatrix.width
        val height = bitMatrix.height
        val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)

        for (x in 0 until width) {
            for (y in 0 until height) {
                bmp.setPixel(x, y, if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE)
            }
        }

        imageView.setImageBitmap(bmp)
    }
}