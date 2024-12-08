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
import com.google.android.gms.tasks.Tasks
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
    private lateinit var btnSeleccionarFotos: Button // Botón para seleccionar fotos

    private var pdfUri: Uri? = null // URI para almacenar el archivo seleccionado
    private val imagenesUris: MutableList<Uri> =
        mutableListOf() // Lista de URIs de imágenes seleccionadas

    companion object {
        private const val PDF_REQUEST_CODE = 100 // Código de solicitud para seleccionar PDF
        private const val IMAGES_REQUEST_CODE = 101 // Código de solicitud para seleccionar imágenes
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
        btnSeleccionarFotos = view.findViewById(R.id.btnSeleccionarFotos) // Inicializar el botón

        // Configurar Spinner con categorías
        configurarSpinner()

        // Acción al presionar el botón para seleccionar PDF
        btnSeleccionarPDF.setOnClickListener { seleccionarPdf() }

        // Acción al presionar el botón para seleccionar fotos
        btnSeleccionarFotos.setOnClickListener { seleccionarFotos() }

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

        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categorias)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategoria.adapter = adapter
    }

    private fun seleccionarPdf() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "application/pdf" // Filtrar solo PDFs
        startActivityForResult(intent, PDF_REQUEST_CODE)
    }

    private fun seleccionarFotos() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*" // Filtrar solo imágenes
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true) // Permitir selección múltiple
        startActivityForResult(intent, IMAGES_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                PDF_REQUEST_CODE -> {
                    data?.data?.let { uri ->
                        pdfUri = uri
                        Toast.makeText(
                            requireContext(),
                            "Archivo seleccionado: ${uri.lastPathSegment}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                IMAGES_REQUEST_CODE -> {
                    data?.let {
                        val clipData = it.clipData
                        if (clipData != null) {
                            // Si se seleccionaron múltiples imágenes
                            if (clipData.itemCount in 4..6) {
                                for (i in 0 until clipData.itemCount) {
                                    val imageUri = clipData.getItemAt(i).uri
                                    imagenesUris.add(imageUri) // Agregar URI de la imagen seleccionada
                                }
                                Toast.makeText(
                                    requireContext(),
                                    "Fotos seleccionadas exitosamente",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    "Selecciona entre 4 y 6 fotos",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            // Si se seleccionó solo una imagen
                            it.data?.let { uri ->
                                imagenesUris.add(uri)
                                if (imagenesUris.size in 4..6) {
                                    Toast.makeText(
                                        requireContext(),
                                        "Foto seleccionada exitosamente",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    Toast.makeText(
                                        requireContext(),
                                        "Selecciona entre 4 y 6 fotos",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    }
                }
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

        // Validaciones individuales para cada campo
        if (titulo.isEmpty()) {
            Toast.makeText(requireContext(), "El título no puede estar vacío", Toast.LENGTH_SHORT)
                .show()
            return
        }
        if (categoria.isEmpty()) {
            Toast.makeText(
                requireContext(),
                "Por favor, selecciona una categoría",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        if (descripcion.isEmpty()) {
            Toast.makeText(
                requireContext(),
                "La descripción no puede estar vacía",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        if (conclusion.isEmpty()) {
            Toast.makeText(
                requireContext(),
                "La conclusión no puede estar vacía",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        if (recomendaciones.isEmpty()) {
            Toast.makeText(
                requireContext(),
                "Las recomendaciones no pueden estar vacías",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        if (idUsuario.isEmpty()) {
            Toast.makeText(requireContext(), "No se pudo obtener el usuario", Toast.LENGTH_SHORT)
                .show()
            return
        }
        if (pdfUri == null) {
            Toast.makeText(
                requireContext(),
                "Por favor, selecciona un archivo PDF",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        if (imagenesUris.size !in 4..6) {
            Toast.makeText(requireContext(), "Selecciona entre 4 y 6 fotos", Toast.LENGTH_SHORT)
                .show()
            return
        }

        val storageRef = FirebaseStorage.getInstance().reference

        // Crear una carpeta única para esta investigación
        val investigacionId = db?.collection("investigaciones")?.document()?.id ?: ""
        val investigacionFolderRef = storageRef.child("investigaciones/$investigacionId")

        // Subir PDF
        val pdfRef = investigacionFolderRef.child("pdf/${pdfUri?.lastPathSegment}")
        pdfRef.putFile(pdfUri!!).addOnSuccessListener {
            pdfRef.downloadUrl.addOnSuccessListener { uri ->
                val pdfUrl = uri.toString()

                // Subir imágenes
                val imagenUrls: MutableList<String> = mutableListOf()
                val imageUploadTasks = imagenesUris.map { imageUri ->
                    val imageRef =
                        investigacionFolderRef.child("imagenes/${imageUri.lastPathSegment}")
                    imageRef.putFile(imageUri).continueWithTask { task ->
                        if (task.isSuccessful) {
                            imageRef.downloadUrl
                        } else {
                            throw task.exception ?: Exception("Error al subir la imagen")
                        }
                    }.addOnSuccessListener { imageUri ->
                        imagenUrls.add(imageUri.toString())
                    }
                }

                // Espera que todas las imágenes se suban antes de guardar la investigación
                Tasks.whenAllSuccess<Uri>(*imageUploadTasks.toTypedArray())
                    .addOnCompleteListener {
                        val investigacion = cls_Investigacion(
                            titulo = titulo,
                            categoria = categoria,
                            descripcion = descripcion,
                            conclusion = conclusion,
                            recomendaciones = recomendaciones,
                            pdfUrl = pdfUrl,
                            idUsuario = idUsuario,
                            imagenes = imagenUrls // Guardar las URLs de las imágenes
                        )

                        // Guardar la investigación en Firestore
                        db?.collection("investigaciones")
                            ?.add(investigacion)
                            ?.addOnSuccessListener {
                                Toast.makeText(
                                    requireContext(),
                                    "Investigación añadida exitosamente",
                                    Toast.LENGTH_SHORT
                                ).show()
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
        }.addOnFailureListener {
            Toast.makeText(requireContext(), "Error al subir el archivo PDF", Toast.LENGTH_SHORT)
                .show()
        }
    }
}