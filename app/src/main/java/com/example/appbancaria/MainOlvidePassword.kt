package com.example.appbancaria

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.Button
import android.widget.EditText

class MainOlvidePassword : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_olvide_password)

        val btnRegresar = findViewById<Button>(R.id.btnRegresar)
        val btnMostrarOcultar = findViewById<Button>(R.id.btnMostrarOcultar)
        val btnRecuperar = findViewById<Button>(R.id.btnRecuperar)
        val edtCorreo = findViewById<EditText>(R.id.edtCorreo)
        val edtPassword = findViewById<EditText>(R.id.edtPassword)

        btnRegresar.setOnClickListener {
            val intent = Intent(this, MainInicioSesion::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
            finish()
        }

        btnMostrarOcultar.setOnClickListener {
            btnMostrarOcultar.isSelected = !btnMostrarOcultar.isSelected
            if (edtPassword.transformationMethod == HideReturnsTransformationMethod.getInstance()) {
                edtPassword.transformationMethod = PasswordTransformationMethod.getInstance()
            } else {
                edtPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
            }
            edtPassword.setSelection(edtPassword.text.length)
        }

        btnRecuperar.setOnClickListener {
            edtPassword.setText("Solicita un restablecimiento seguro")
        }
    }
}
