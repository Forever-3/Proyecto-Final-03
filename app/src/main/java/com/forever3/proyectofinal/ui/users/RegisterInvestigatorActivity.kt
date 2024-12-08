package com.forever3.proyectofinal.ui.users
import com.forever3.proyectofinal.R

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.EditText
import android.widget.Button
import android.widget.Toast
import android.widget.Spinner
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.Date

class RegisterInvestigatorActivity : AppCompatActivity() {

    var auth = FirebaseAuth.getInstance()
    var db = FirebaseFirestore.getInstance()
    var storage = FirebaseStorage.getInstance()

    private lateinit var txtRNombreInvestigator: EditText
    private lateinit var txtREmailInvestigator: EditText
    private lateinit var txtRContraInvestigator: EditText
    private lateinit var txtRReContraInvestigator: EditText
    private lateinit var spnRGradeInvestigator: Spinner
    private lateinit var txtRDescriptionInvestigator: EditText
    private lateinit var btnRegistrarInvestigator: Button
    private lateinit var btnRegistrarInvestigatorGoogle: Button
    private lateinit var btnSeleccionarImagen: Button


    private var imageUri: Uri? = null // Variable para almacenar la URI de la imagen seleccionada
    private val PICK_IMAGE_REQUEST = 1 // Código de solicitud para la selección de imagen

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register_investigator)

        // Enlazar vistas con IDs
        txtRNombreInvestigator = findViewById(R.id.txtInvestigatorName)
        txtREmailInvestigator = findViewById(R.id.txtInvestigatorEmail)
        txtRContraInvestigator = findViewById(R.id.txtInvestigatorPassword)
        txtRReContraInvestigator = findViewById(R.id.txtInvestigatorConfirmPassword)
        spnRGradeInvestigator = findViewById(R.id.spnInvestigatorGrade)
        txtRDescriptionInvestigator = findViewById(R.id.txtInvestigatorDescription)
        btnRegistrarInvestigator = findViewById(R.id.btnInvestigatorRegister)
        btnRegistrarInvestigatorGoogle = findViewById(R.id.btnInvestigatorGoogleRegister)
        btnSeleccionarImagen = findViewById(R.id.btnSeleccionarImagen)

        btnRegistrarInvestigator.setOnClickListener {
            if (imageUri == null) {
                Toast.makeText(this, "Seleccione una imagen antes de continuar.", Toast.LENGTH_SHORT).show()
            } else {
                registrarUsuario()
            }
        }

        // Configurar el botón para seleccionar imagen
        btnSeleccionarImagen.setOnClickListener {
            openGallery()
        }
    }
    // Abre la galería para seleccionar una imagen
    private fun openGallery(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*" // Solo se abre imagenes
        startActivityForResult(intent, PICK_IMAGE_REQUEST) //espera el resultado
    }

    //Recibe la imagen
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            imageUri = data.data // Guarda la URI de la imagen seleccionada
            Toast.makeText(this, "Imagen seleccionada  correctamente", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "No se seleccionó ninguna imagen", Toast.LENGTH_SHORT).show()
        }
    }

    private fun registrarUsuario() {
        val nombre = txtRNombreInvestigator.text.toString()
        val email = txtREmailInvestigator.text.toString()
        val contra = txtRContraInvestigator.text.toString()
        val reContra = txtRReContraInvestigator.text.toString()
        val grade = spnRGradeInvestigator.selectedItem.toString()
        val description = txtRDescriptionInvestigator.text.toString()

        if (nombre.isEmpty() || email.isEmpty() || contra.isEmpty() || reContra.isEmpty() || grade == "Selecciona tu grado" || description.isEmpty()) {
            Toast.makeText(this, "Favor de llenar todos los campos", Toast.LENGTH_SHORT).show()
        } else {
            if (contra == reContra) {
                auth.createUserWithEmailAndPassword(email, contra)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val dt: Date = Date()
                            val userId = task.result?.user?.uid
                            val storageRef = storage.reference.child("usuario/$userId/profile.jpg")
                            storageRef.putFile(imageUri!!)
                                .addOnSuccessListener {
                                    storageRef.downloadUrl.addOnSuccessListener { uri ->
                                        // Registrar al usuario con la URL de la imagen
                                        val user = hashMapOf(
                                            "idUsuario" to userId,
                                            "nombre" to nombre,
                                            "email" to email,
                                            "grado" to grade,
                                            "rol" to "investigador",
                                            "descripcion" to description,
                                            "ultAcceso" to Date().toString(),
                                            "fotoPerfil" to uri.toString()
                                        )
                                        db.collection("usuarios")
                                            .add(user)
                                            .addOnSuccessListener { documentReference ->
                                                // Guardar datos en el almacenamiento local
                                                val prefe = this.getSharedPreferences("appData", Context.MODE_PRIVATE)
                                                val editor = prefe.edit()
                                                editor.putString("email", email)
                                                editor.putString("contra", contra)
                                            //    editor.putString("fotoUrl", uri.toString()) // Guardar la URL de la imagen
                                                editor.apply()

                                                Toast.makeText(this, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show()
                                                val intent = Intent(this, LoginActivity::class.java)
                                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                                                startActivity(intent)
                                                finish()
                                            }
                                            .addOnFailureListener { e ->
                                                Toast.makeText(this, "Error al registrar usuario en Firestore", Toast.LENGTH_SHORT).show()
                                            }
                                    }
                                }
                                .addOnFailureListener {
                                    Toast.makeText(this, "Error al subir la imagen.", Toast.LENGTH_SHORT).show()
                                }
                        } else {
                            Toast.makeText(this, "Error al registrar usuario: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Las contraseñas no coinciden.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}