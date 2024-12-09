package com.forever3.proyectofinal.ui.logout

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.forever3.proyectofinal.R
import com.google.firebase.auth.FirebaseAuth
import android.content.Intent
import com.forever3.proyectofinal.ui.users.LoginActivity

class logoutFragment : Fragment() {

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar el layout del fragmento
        val rootView = inflater.inflate(R.layout.fragment_logout, container, false)

        // Inicializar FirebaseAuth
        auth = FirebaseAuth.getInstance()

        // Configurar el botón de logout
        val btnLogout = rootView.findViewById<Button>(R.id.btnLogout)
        btnLogout.setOnClickListener {
            showLogoutConfirmationDialog()
        }

        return rootView
    }

    // Mostrar el diálogo de confirmación de logout
    private fun showLogoutConfirmationDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Cerrar sesión")
        builder.setMessage("¿Estás seguro de que deseas cerrar sesión?")
        builder.setCancelable(false)

        builder.setPositiveButton("Sí") { dialog, _ ->
            // Desconectar al usuario de Firebase
            FirebaseAuth.getInstance().signOut()

            // Redirigir a la pantalla de login
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish() // Finaliza la actividad actual para evitar que el usuario regrese

            dialog.dismiss() // Cierra el diálogo
        }

        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss() // Solo cierra el diálogo si el usuario no desea salir
        }

        val dialog = builder.create()
        dialog.show()
    }
}
