package com.forever3.proyectofinal.ui.addInvestigation

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.forever3.proyectofinal.R
import com.forever3.proyectofinal.ui.home.cls_Investigacion
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class AgregarInvestigacionFragment : Fragment() {

    private var db: FirebaseFirestore? = null
    private var auth: FirebaseAuth? = null

    private lateinit var etTitulo: EditText
    private lateinit var spinnerCategoria: Spinner
    private lateinit var etDescripcion: EditText
    private lateinit var etConclusion: EditText
    private lateinit var etRecomendaciones: EditText
    private lateinit var btnGuardar: Button
    private lateinit var btnSeleccionarPDF: Button

    private var pdfUri: Uri? = null // URI para almacenar el archivo seleccionado

    companion object {
        private const val PDF_REQUEST_CODE = 100 // Código de solicitud para seleccionar PDF
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_agregar_investigacion, container, false)

        // Inicializar Firebase
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Vincular vistas
        etTitulo = view.findViewById(R.id.etTitulo)
        spinnerCategoria = view.findViewById(R.id.spinnerCategoria)
        etDescripcion = view.findViewById(R.id.etDescripcion)
        etConclusion = view.findViewById(R.id.etConclusion)
        etRecomendaciones = view.findViewById(R.id.etRecomendaciones)
        btnGuardar = view.findViewById(R.id.btnGuardar)
        btnSeleccionarPDF = view.findViewById(R.id.btnSeleccionarPDF)

        // Configurar Spinner con categorías
        configurarSpinner()

        // Acción al presionar el botón para seleccionar PDF
        btnSeleccionarPDF.setOnClickListener { seleccionarPdf() }

        // Acción al presionar el botón para guardar investigación
        btnGuardar.setOnClickListener { agregarInvestigacion() }

        return view
    }

    private fun configurarSpinner() {
        val categorias = listOf(
            "Ingeniería", "Derecho y Política", "Tecnología y Computación",
            "Artes y Humanidades", "Ciencias Naturales", "Salud y Medicina",
            "Ciencias Sociales", "Negocios y Finanzas"
        )

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categorias)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategoria.adapter = adapter
    }

    private fun seleccionarPdf() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "application/pdf" // Filtrar solo PDFs
        startActivityForResult(intent, PDF_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == PDF_REQUEST_CODE) {
            data?.data?.let { uri ->
                pdfUri = uri // Guardar la URI del archivo seleccionado
                Toast.makeText(requireContext(), "Archivo seleccionado: ${uri.lastPathSegment}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun agregarInvestigacion() {
        val titulo = etTitulo.text.toString()
        val categoria = spinnerCategoria.selectedItem.toString()
        val descripcion = etDescripcion.text.toString()
        val conclusion = etConclusion.text.toString()
        val recomendaciones = etRecomendaciones.text.toString()

        val user = auth?.currentUser
        val idUsuario = user?.uid ?: ""

        if (titulo.isNotEmpty() && categoria.isNotEmpty() && descripcion.isNotEmpty() &&
            conclusion.isNotEmpty() && recomendaciones.isNotEmpty() && idUsuario.isNotEmpty() && pdfUri != null
        ) {
            val storageRef = FirebaseStorage.getInstance().reference
            val pdfRef = storageRef.child("investigaciones/${pdfUri?.lastPathSegment}")

            pdfRef.putFile(pdfUri!!)
                .addOnSuccessListener {
                    pdfRef.downloadUrl.addOnSuccessListener { uri ->
                        val pdfUrl = uri.toString()

                        val investigacion = cls_Investigacion(
                            titulo = titulo,
                            categoria = categoria,
                            descripcion = descripcion,
                            conclusion = conclusion,
                            recomendaciones = recomendaciones,
                            pdfUrl = pdfUrl,
                            idUsuario = idUsuario
                        )

                        db?.collection("investigaciones")
                            ?.add(investigacion)
                            ?.addOnSuccessListener {
                                Toast.makeText(
                                    requireContext(),
                                    "Investigación añadida exitosamente",
                                    Toast.LENGTH_SHORT
                                ).show()

                                // Navegar al fragmento principal después de guardar
                                findNavController().navigate(R.id.navigation_home)
                            }
                            ?.addOnFailureListener {
                                Toast.makeText(
                                    requireContext(),
                                    "Error al agregar la investigación",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Error al subir el archivo PDF", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(requireContext(), "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
        }
    }
}
