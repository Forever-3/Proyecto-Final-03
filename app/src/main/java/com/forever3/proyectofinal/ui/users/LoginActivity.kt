package com.forever3.proyectofinal.ui.users

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.forever3.proyectofinal.MainActivity
import com.forever3.proyectofinal.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date

class LoginActivity : AppCompatActivity() {

    private var auth = FirebaseAuth.getInstance()
    private var db = FirebaseFirestore.getInstance()

    private lateinit var btnAutenticar: Button
    private lateinit var txtEmail: EditText
    private lateinit var txtContra: EditText
    private lateinit var txtRegister: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btnAutenticar = findViewById(R.id.btnAutenticar)
        txtEmail = findViewById(R.id.txtEmail)
        txtContra = findViewById(R.id.txtContra)
        txtRegister = findViewById(R.id.txtRegister)

        btnAutenticar.setOnClickListener {
            val email = txtEmail.text.toString().trim()
            val password = txtContra.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        saveUserData(email, password)
                        redirectToMainActivity()
                    } else {
                        showToast("Error: No se pudo autenticar el usuario. Verifica tus datos.")
                    }
                }
            } else {
                showToast("Error: El correo electrónico y la contraseña no pueden estar vacíos.")
            }
        }

        // Conectar el clic en txtRegister con la función de navegación
        txtRegister.setOnClickListener {
            goToRoleSelectionActivity()
        }
    }

    private fun saveUserData(email: String, password: String) {
        val prefe = getSharedPreferences("appData", Context.MODE_PRIVATE)
        val editor = prefe.edit()
        editor.putString("email", email)
        editor.putString("contra", password)
        editor.apply()
    }

    private fun redirectToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    // Mostrar mensajes con Toast
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    // Función para redirigir a RoleSelectionActivity
    private fun goToRoleSelectionActivity() {
        val intent = Intent(this, RoleSelectionActivity::class.java)
        startActivity(intent)
    }
}
