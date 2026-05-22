package com.example.pineladrin_tfg

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions

class LectorQRActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lector_qr)

        iniciarEscaneo()
    }

    private fun iniciarEscaneo() {
        val options = ScanOptions()
        options.setPrompt("Escanea el QR de la reserva")
        options.setBeepEnabled(true)
        options.setOrientationLocked(false)

        barcodeLauncher.launch(options)
    }

    private val barcodeLauncher = registerForActivityResult(ScanContract()) { result ->
        if (result.contents == null) {
            Toast.makeText(this, "Escaneo cancelado", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            val idReserva = result.contents
            actualizarReserva(idReserva)
        }
    }

    private fun actualizarReserva(idReserva: String) {
        db.collection("reservas")
            .document(idReserva)
            .update("qrLeido", "SI")
            .addOnSuccessListener {
                Toast.makeText(this, "Acceso validado correctamente", Toast.LENGTH_LONG).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error: reserva no encontrada", Toast.LENGTH_LONG).show()
                finish()
            }
    }
}