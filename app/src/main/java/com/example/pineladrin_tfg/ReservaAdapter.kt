package com.example.pineladrin_tfg

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ReservaAdapter(
    private val lista: List<Reserva>,
    private val onClick: (Reserva) -> Unit,
    private val onDelete: (Reserva) -> Unit
) : RecyclerView.Adapter<ReservaAdapter.ReservaViewHolder>() {

    class ReservaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtFecha: TextView = view.findViewById(R.id.txtFecha)
        val txtPlaza: TextView = view.findViewById(R.id.txtPlaza)
        val btnEliminar: Button = view.findViewById(R.id.btnEliminar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReservaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_reserva, parent, false)
        return ReservaViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReservaViewHolder, position: Int) {
        val reserva = lista[position]

        holder.txtFecha.text = reserva.fecha
        holder.txtPlaza.text = "Plaza: ${reserva.plaza}"

        holder.itemView.setOnClickListener { onClick(reserva) }
        holder.btnEliminar.setOnClickListener { onDelete(reserva) }
    }

    override fun getItemCount(): Int = lista.size
}