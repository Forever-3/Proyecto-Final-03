package com.forever3.proyectofinal.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.forever3.proyectofinal.R

class ImagenesAdapter(
    private val context: Context,
    private val imagenes: List<String> // Aquí pasas una lista de URLs de las imágenes
) : RecyclerView.Adapter<ImagenesAdapter.ImagenViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagenViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_imagen, parent, false)
        return ImagenViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImagenViewHolder, position: Int) {
        val imagenUrl = imagenes[position]
        Glide.with(context)
            .load(imagenUrl)  // Aquí cargas la imagen desde una URL
            .into(holder.imgView)
    }

    override fun getItemCount(): Int = imagenes.size

    class ImagenViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgView: ImageView = itemView.findViewById(R.id.imageView)
    }
}
