package com.example.appbancaria

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast

class MainInicioSesion : AppCompatActivity() {
    lateinit var edtPassword: EditText
    lateinit var edtCorreo: EditText
    lateinit var pruebaSpinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio_sesion)

        val btnMostrarOcultar = findViewById<Button>(R.id.btnMostrarOcultar)
        val btnRegistrarse = findViewById<Button>(R.id.btnRegistrarse)
        val tvOlvidar = findViewById<TextView>(R.id.tvOlvidar)
        val btnEntrar = findViewById<Button>(R.id.btnEntrar)

        edtPassword = findViewById(R.id.edtPassword)
        edtCorreo = findViewById(R.id.edtCorreo)

        btnMostrarOcultar.setOnClickListener {
            btnMostrarOcultar.isSelected = !btnMostrarOcultar.isSelected
            if (edtPassword.transformationMethod == HideReturnsTransformationMethod.getInstance()) {
                edtPassword.transformationMethod = PasswordTransformationMethod.getInstance()
            } else {
                edtPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
            }
            edtPassword.setSelection(edtPassword.text.length)
        }

        btnRegistrarse.setOnClickListener {
            val intent = Intent(this, MainRegistro::class.java)
            startActivity(intent)
        }

        tvOlvidar.setOnClickListener {
            val intent = Intent(this, MainOlvidePassword::class.java)
            startActivity(intent)
        }

        btnEntrar.setOnClickListener {
            if (testData()){
                val correo = edtCorreo.text.toString()
                val password = edtPassword.text.toString()

                if (comprobar(correo, password)) {
                    val intent = Intent(this, MainInicioCuenta::class.java)
                    intent.putExtra("correo", correo)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                    cleanForm()
                }
            }else{
                Toast.makeText(this,"Todos los datos son obligatorios",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun comprobar(correo: String, password: String): Boolean {
        val sqlManager = SQLManager(this)
        return sqlManager.autenticar(correo, password)
    }

    fun testData():Boolean{
        var response = true
        if (edtCorreo.text.isEmpty() || edtPassword.text.isEmpty()){
            response = false
        }
        return response
    }

    fun cleanForm(){
        edtPassword.text.clear()
    }
}
