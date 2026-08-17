package com.example.appbancaria

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputFilter
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.Random

class MainRegistro : AppCompatActivity() {
    lateinit var edtNumeroCuenta: EditText
    lateinit var edtNombre: EditText
    lateinit var edtPassword: EditText
    lateinit var edtTelefono: EditText
    lateinit var edtFechaAlta: EditText
    lateinit var edtCorreo: EditText
    lateinit var edtSaldoInicial: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_registro)

        val btnIniciarSesion = findViewById<Button>(R.id.btnIniciarSesion)
        val formatoFecha = "dd/MM/yyyy"
        val formato = SimpleDateFormat(formatoFecha, Locale.getDefault())
        val fechaActual = Calendar.getInstance().time
        val fechaFormateada = formato.format(fechaActual)
        val btnGenerarCuenta = findViewById<Button>(R.id.btnGenerarCuenta)
        val btnMostrarOcultar = findViewById<Button>(R.id.btnMostrarOcultar)

        val btnRegresar = findViewById<Button>(R.id.btnRegresar)
        val btnRegistrarse = findViewById<Button>(R.id.btnRegistrarse)

        edtNumeroCuenta = findViewById(R.id.edtNumeroCuenta)
        edtNombre = findViewById(R.id.edtNombre)
        edtPassword = findViewById(R.id.edtPassword)
        edtTelefono = findViewById(R.id.edtTelefono)
        edtFechaAlta = findViewById(R.id.edtFechaAlta)
        edtCorreo = findViewById(R.id.edtCorreo)
        edtSaldoInicial = findViewById(R.id.edtSaldoInicial)

        edtFechaAlta.setText(fechaFormateada)

        btnIniciarSesion.setOnClickListener {
            val intent = Intent(this, MainInicioSesion::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
            finish()
        }

        btnGenerarCuenta.setOnClickListener {
            val numeroCuenta = generarNumeroCuenta()
            edtNumeroCuenta.setText(numeroCuenta)
        }

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

        val maxLength = 10
        val inputFilter = InputFilter { source, _, _, _, _, _ ->
            val currentText = edtTelefono.text.toString()
            val inputText = source.toString()
            val totalText = currentText + inputText
            if (totalText.length <= maxLength && totalText.matches(Regex("\\d*"))) {
                null
            } else {
                ""
            }
        }
        edtTelefono.filters = arrayOf(inputFilter)

        btnRegistrarse.setOnClickListener {
            if (testData()){
                val correo = edtCorreo.text.toString().trim()
                val telefono = edtTelefono.text.toString()
                val saldo = edtSaldoInicial.text.toString().toDoubleOrNull()
                if (!correo.contains("@") || telefono.length != 10 || saldo == null || saldo < 0) {
                    Toast.makeText(this, "Revisa correo, teléfono y saldo", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                val numeroCuenta2 = generarNumeroCuenta()
                val saldoInicial2 = 10000.0
                val numeroCuenta = edtNumeroCuenta.text.toString().toLongOrNull()
                if (numeroCuenta == null) {
                    Toast.makeText(this, "Genera un número de cuenta válido", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                val datos1 = UsuariosClass(correo,edtNombre.text.toString(),edtPassword.text.toString(),telefono.toLong(),edtFechaAlta.text.toString())
                val datos2 = CuentasClass(correo,numeroCuenta,saldo)
                val datos3 = CuentasClass(correo,numeroCuenta2.toLong(),saldoInicial2)
                var sqlManager = SQLManager(this)
                val responseCuenta = sqlManager.controlCuentas(this,numeroCuenta)
                val responseCuenta2 = sqlManager.controlCuentas(this,numeroCuenta2.toLong())
                val responseCorreo = sqlManager.controlUsuariosCorreo(this,correo)
                val responseTelefono = sqlManager.controlUsuariosTelefono(this,telefono.toLong())
                if(responseCorreo){
                    Toast.makeText(this,"Este correo ya existe", Toast.LENGTH_SHORT).show()
                }else{
                    if(responseTelefono){
                        Toast.makeText(this,"Este telefono ya existe", Toast.LENGTH_SHORT).show()
                    }else if (responseCuenta || responseCuenta2) {
                        Toast.makeText(this,"Una de las cuentas ya existe", Toast.LENGTH_SHORT).show()
                    }else{
                        if(!sqlManager.registrarUsuarioConCuentas(datos1, datos2, datos3)){
                            Toast.makeText(this,"Hubo un error al grabar los datos", Toast.LENGTH_SHORT).show()
                            cleanForm()
                        }else{
                            Toast.makeText(this,"La operación se realizó con éxito", Toast.LENGTH_SHORT).show()
                            cleanForm()
                            val intent = Intent(this, MainInicioSesion::class.java)
                            intent.addFlags( Intent.FLAG_ACTIVITY_SINGLE_TOP)
                            startActivity(intent)
                            finish()
                        }
                    }
                }
            }else{
                Toast.makeText(this,"Todos los datos son obligatorios",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun testData():Boolean{
        var response = true
        if (edtNumeroCuenta.text.isEmpty() || edtNombre.text.isEmpty() || edtPassword.text.isEmpty() || edtTelefono.text.isEmpty() || edtFechaAlta.text.isEmpty() || edtCorreo.text.isEmpty() || edtSaldoInicial.text.isEmpty()){
            response = false
        }
        return response
    }

    fun cleanForm(){
        edtNombre.text.clear()
        edtPassword.text.clear()
        edtTelefono.text.clear()
        edtCorreo.text.clear()
        edtSaldoInicial.text.clear()
    }

    private fun generarNumeroCuenta(): String {
        val longitudCuenta = 10
        val numeroCuenta = StringBuilder()

        val random = Random()
        for (i in 0 until longitudCuenta) {
            val digito = random.nextInt(10)
            numeroCuenta.append(digito)
        }

        return numeroCuenta.toString()
    }
}
