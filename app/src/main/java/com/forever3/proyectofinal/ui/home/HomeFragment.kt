package com.forever3.proyectofinal.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.forever3.proyectofinal.R
import com.forever3.proyectofinal.databinding.FragmentHomeBinding
import com.forever3.proyectofinal.ui.home.detallesInvestigacionFragment
import com.google.firebase.firestore.FirebaseFirestore
import com.forever3.proyectofinal.ui.home.HomeFragmentDirections

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: FirebaseFirestore
    private lateinit var adapter: InvestigacionAdapter
    private val listaInvestigaciones = mutableListOf<cls_Investigacion>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        db = FirebaseFirestore.getInstance()

        binding.rvInvestigaciones.layoutManager = LinearLayoutManager(requireContext())
        adapter = InvestigacionAdapter(listaInvestigaciones) { investigacion ->
            mostrarDetallesInvestigacion(investigacion)
        }
        binding.rvInvestigaciones.adapter = adapter

        obtenerDatosDeFirebase()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun obtenerDatosDeFirebase() {
        db.collection("investigaciones")
            .get()
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
