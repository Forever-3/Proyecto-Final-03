package com.forever3.proyectofinal.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AdapterView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.forever3.proyectofinal.R
import com.forever3.proyectofinal.databinding.FragmentHomeBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.forever3.proyectofinal.ui.home.HomeFragmentDirections

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: FirebaseFirestore
    private lateinit var adapter: InvestigacionAdapter
    private val listaInvestigaciones = mutableListOf<cls_Investigacion>()
    // Reorganizamos las categorías para que "Todas" sea la primera opción
    private val categorias = listOf(
        "Todas", "Ingeniería", "Derecho y Política", "Tecnología y Computación",
        "Artes y Humanidades", "Ciencias Naturales", "Salud y Medicina",
        "Ciencias Sociales", "Negocios y Finanzas"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        db = FirebaseFirestore.getInstance()

        // Configuramos el Spinner para seleccionar la categoría
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categorias)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCategoria.adapter = spinnerAdapter

        // Configuración del RecyclerView
        binding.rvInvestigaciones.layoutManager = LinearLayoutManager(requireContext())
        adapter = InvestigacionAdapter(listaInvestigaciones) { investigacion ->
            mostrarDetallesInvestigacion(investigacion)
        }
        binding.rvInvestigaciones.adapter = adapter

        // Escuchamos el cambio en el Spinner para aplicar el filtro
        binding.spinnerCategoria.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // Obtener la categoría seleccionada y cargar las investigaciones
                val categoriaSeleccionada = parent?.getItemAtPosition(position).toString()
                obtenerDatosDeFirebase(categoriaSeleccionada)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Aquí no es necesario hacer nada, pero se debe implementar
            }
        })

        // Cargar todas las investigaciones inicialmente
        obtenerDatosDeFirebase("Todas")

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun obtenerDatosDeFirebase(categoria: String) {
        // Filtrar según la categoría seleccionada
        val query = if (categoria == "Todas") {
            db.collection("investigaciones")
        } else {
            db.collection("investigaciones").whereEqualTo("categoria", categoria)
        }

        query.get()
            .addOnSuccessListener { documentos ->
                listaInvestigaciones.clear()
                for (documento in documentos) {
                    val investigacion = documento.toObject(cls_Investigacion::class.java)
                    listaInvestigaciones.add(investigacion)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error al cargar datos: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun mostrarDetallesInvestigacion(investigacion: cls_Investigacion) {
        // Utilizamos Safe Args para pasar los argumentos al fragmento de detalles
        val action = HomeFragmentDirections
            .actionHomeFragmentToDetallesInvestigacionFragment(investigacion)
        findNavController().navigate(action)
    }
}
