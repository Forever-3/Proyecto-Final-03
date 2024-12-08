package com.forever3.proyectofinal.ui.home

import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class cls_Investigacion(
    val categoria: String = "",
    val conclusion: String = "",
    val descripcion: String = "",
    val idUsuario: String = "",
    val imagenes: List<String> = emptyList(),
    val pdfUrl: String = "",
    val recomendaciones: String = "",
    val titulo: String = ""
) : Parcelable
