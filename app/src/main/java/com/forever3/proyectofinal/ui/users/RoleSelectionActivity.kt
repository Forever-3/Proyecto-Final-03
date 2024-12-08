package com.forever3.proyectofinal.ui.users

import android.os.Bundle
import android.content.Intent
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.forever3.proyectofinal.R

class RoleSelectionActivity : AppCompatActivity() {

    private lateinit var btnGoLector: Button
    private lateinit var btnGoInvestigator: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_role_selection)

        btnGoLector = findViewById(R.id.btnGoLector)
        btnGoInvestigator = findViewById(R.id.btnGoInvestigator)

        btnGoLector.setOnClickListener {
            goToRegisterLectorActivity()
        }

        btnGoInvestigator.setOnClickListener {
            goToRegisterInvestigatorActivity()
        }
    }

    private fun goToRegisterLectorActivity() {
        val intent = Intent(this, RegisterLectorActivity::class.java)
        startActivity(intent)
    }

    private fun goToRegisterInvestigatorActivity() {
        val intent = Intent(this, RegisterInvestigatorActivity::class.java)
        startActivity(intent)
    }
}
