package com.example.pineladrin_tfg

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter

class ConfirmacionActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirmacion)

        val btnVolver = findViewById<Button>(R.id.btnVolver)
        val txtMatricula = findViewById<TextView>(R.id.txtMatricula)
        val txtPlaza = findViewById<TextView>(R.id.txtPlaza)
        val txtTipo = findViewById<TextView>(R.id.txtTipo)
        val txtCorreo = findViewById<TextView>(R.id.txtCorreo)
        val txtFecha = findViewById<TextView>(R.id.txtFecha)
        val imageQR = findViewById<ImageView>(R.id.imageQR)

        // Datos recibidos
        val matricula = intent.getStringExtra("matricula") ?: ""
        val plaza = intent.getStringExtra("plazaSeleccionada") ?: ""
        val tipo = intent.getStringExtra("tipoPlaza") ?: ""
        val correo = intent.getStringExtra("correo") ?: ""
        val fecha = intent.getStringExtra("fecha") ?: ""

        val fechaISO = convertirFechaISO(fecha)

        // Mostrar datos
        txtMatricula.text = "Matrícula registrada: $matricula"
        txtPlaza.text = "Plaza seleccionada: $plaza"
        txtTipo.text = "Tipo de plaza: $tipo"
        txtCorreo.text = "Reservada por: $correo"
        txtFecha.text = "Fecha de reserva: $fecha"

        if (correo.isEmpty()) {
            Toast.makeText(this, "Error: correo no recibido", Toast.LENGTH_LONG).show()
            return
        }

        // Comprobar si ya tiene reserva ese día
        db.collection("reservas")
            .whereEqualTo("correo", correo)
            .whereEqualTo("fechaISO", fechaISO)
            .get()
            .addOnSuccessListener { docsUsuario ->
                if (!docsUsuario.isEmpty) {
                    Toast.makeText(this, "Ya tienes una reserva para ese día.", Toast.LENGTH_LONG).show()
                    return@addOnSuccessListener
                }

                // Comprobar si la plaza está ocupada ese día
                db.collection("reservas")
                    .whereEqualTo("plaza", plaza)
                    .whereEqualTo("fechaISO", fechaISO)
                    .get()
                    .addOnSuccessListener { docsPlaza ->
                        if (!docsPlaza.isEmpty) {
                            Toast.makeText(this, "Esa plaza ya está reservada. Prueba con otra.", Toast.LENGTH_LONG).show()
                            return@addOnSuccessListener
                        }

                        // Guardar reserva
                        val reserva = hashMapOf(
                            "matricula" to matricula,
                            "plaza" to plaza,
                            "tipo" to tipo,
                            "correo" to correo,
                            "fecha" to fecha,
                            "fechaISO" to fechaISO,
                            "qrLeido" to "NO",
                            "timestamp" to System.currentTimeMillis()
                        )

                        db.collection("reservas")
                            .add(reserva)
                            .addOnSuccessListener { document ->
                                Toast.makeText(this, "Reserva guardada correctamente", Toast.LENGTH_SHORT).show()
                                generarQR(document.id, imageQR)
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Error al guardar la reserva", Toast.LENGTH_SHORT).show()
                            }
                    }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al comprobar reservas", Toast.LENGTH_SHORT).show()
            }

        // 🔥 BOTÓN IR AL MENÚ PRINCIPAL
        btnVolver.setOnClickListener {
            val intent = Intent(this, MenuPrincipalActivity::class.java)
            intent.putExtra("correo", correo)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun convertirFechaISO(fecha: String): String {
        val partes = fecha.split("/")
        val dia = partes[0].padStart(2, '0')
        val mes = partes[1].padStart(2, '0')
        val año = partes[2]
        return "$año-$mes-$dia"
    }

    private fun generarQR(texto: String, imageView: ImageView) {
        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(texto, BarcodeFormat.QR_CODE, 600, 600)

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