package com.forever3.proyectofinal.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.forever3.proyectofinal.R

//Titulo
//categoria
//descripcion

class InvestigacionAdapter(
    private val listaInvestigaciones: List<cls_Investigacion>,
    private val onItemClickListener: (cls_Investigacion) -> Unit
) : RecyclerView.Adapter<InvestigacionAdapter.InvestigacionViewHolder>() {

    // Clase interna para vincular las vistas
    class InvestigacionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitulo: TextView = itemView.findViewById(R.id.tvTitulo)
        val tvDescripcion: TextView = itemView.findViewById(R.id.tvDescripcion)
        val tvCategoria: TextView = itemView.findViewById(R.id.tvCategoria)
    }

    // Crear la vista para cada elemento del RecyclerView
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InvestigacionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_investigacion, parent, false)
        return InvestigacionViewHolder(view)
    }

    // Vincular los datos con la vista
    override fun onBindViewHolder(holder: InvestigacionViewHolder, position: Int) {
        val investigacion = listaInvestigaciones[position]
        holder.tvTitulo.text = investigacion.titulo
        holder.tvDescripcion.text = "Descripcion: ${investigacion.descripcion}"
        holder.tvCategoria.text = "Categoria: ${investigacion.categoria}"
        holder.itemView.setOnClickListener {
            onItemClickListener(investigacion)  // Invoca el callback cuando se hace clic
        }
    }

    // Devolver el número total de elementos
    override fun getItemCount(): Int = listaInvestigaciones.size
}
