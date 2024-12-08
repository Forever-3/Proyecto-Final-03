package com.forever3.proyectofinal.ui.home  // Actualiza el paquete según la estructura de tu proyecto

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.forever3.proyectofinal.R
import com.forever3.proyectofinal.ui.home.cls_Investigacion  // Asegúrate de que el modelo esté en el paquete correcto

class detallesInvestigacionFragment : Fragment() {

    // Vistas para mostrar los detalles
    private lateinit var tvTitulo: TextView
    private lateinit var tvCategoria: TextView
    private lateinit var tvDescripcion: TextView
    private lateinit var tvRecomendaciones: TextView
    private lateinit var tvConclusion: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_detalles_investigacion, container, false)

        // Inicializar las vistas
        tvTitulo = view.findViewById(R.id.tvTitulo)
        tvCategoria = view.findViewById(R.id.tvCategoria)
        tvDescripcion = view.findViewById(R.id.tvDescripcion)
        tvRecomendaciones = view.findViewById(R.id.tvRecomendaciones)
        tvConclusion = view.findViewById(R.id.tvConclusion)

        // Obtener los datos pasados a través del bundle
        val investigacion = arguments?.getParcelable<cls_Investigacion>("investigacion")

        // Mostrar los detalles en las vistas
        investigacion?.let {
            tvTitulo.text = it.titulo
            tvCategoria.text = "Categoría: ${it.categoria}"
            tvDescripcion.text = "Descripción: ${it.descripcion}"
            tvRecomendaciones.text = "Recomendaciones: ${it.recomendaciones}"
            tvConclusion.text = "Conclusión: ${it.conclusion}"
        }

        return view
    }

    companion object {
        fun newInstance(investigacion: cls_Investigacion): detallesInvestigacionFragment {
            val fragment = detallesInvestigacionFragment()
            val bundle = Bundle()
            bundle.putParcelable("investigacion", investigacion)  // Pasa los datos como Parcelable
            fragment.arguments = bundle
            return fragment
        }
    }
}
