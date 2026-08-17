package com.example.appbancaria

import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import java.util.Locale
import java.util.Random

class MainFacturaSinRetiro2 : AppCompatActivity() {
    lateinit var fechaHora: TextView
    lateinit var fechaFechaVencimiento: TextView
    lateinit var edtClaveRetiro1: EditText
    lateinit var edtClaveRetiro2: EditText
    lateinit var edtClaveRetiro3: EditText
    lateinit var edtClaveSeguridad: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_factura_sin_retiro2)

        val btnSalir = findViewById<Button>(R.id.btnSalir)

        val correo = intent.getStringExtra("correo")
        val importe = intent.getDoubleExtra("importe",0.0)

        val tvImporte = findViewById<TextView>(R.id.tvImporte)
        tvImporte.text = "$$importe"

        fechaHora = findViewById(R.id.tvFechayHora)
        fechaFechaVencimiento = findViewById(R.id.tvFechaVencimiento)

        val calendar = Calendar.getInstance()
        val currentTime = calendar.time

        val fechaFormato = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val horaFormateada = fechaFormato.format(currentTime)

        val formatoFecha = "d 'de' MMMM 'del' yyyy"
        val formato = SimpleDateFormat(formatoFecha, Locale.getDefault())
        val fechaFormateada = formato.format(calendar)

        val concat = "$fechaFormateada ,$horaFormateada h"
        fechaHora.text = concat

        val fechaFutura = calendar.clone() as Calendar
        fechaFutura.add(Calendar.DAY_OF_MONTH, 3)

        val fechaFormateadaFutura = formato.format(fechaFutura.time)
        fechaFechaVencimiento.text = "Fecha de vencimiento: $fechaFormateadaFutura"

        edtClaveRetiro1 = findViewById(R.id.edtClaveRetiro1)
        edtClaveRetiro2 = findViewById(R.id.edtClaveRetiro2)
        edtClaveRetiro3 = findViewById(R.id.edtClaveRetiro3)
        edtClaveSeguridad = findViewById(R.id.edtClaveSeguridad)

        val clave1 = claveRetiro()
        edtClaveRetiro1.setText(clave1)

        val clave2 = claveRetiro()
        edtClaveRetiro2.setText(clave2)

        val clave3 = claveRetiro()
        edtClaveRetiro3.setText(clave3)

        val clave4 = claveRetiro()
        edtClaveSeguridad.setText(clave4)

        btnSalir.setOnClickListener{
            val intent = Intent(this, MainInicioCuenta::class.java)
            intent.putExtra("correo", correo)
            startActivity(intent)
            finish()
        }
    }

    private fun claveRetiro(): String {
        val longitudClave = 4
        val numeroClave = StringBuilder()

        val random = Random()
        for (i in 0 until longitudClave) {
            val digito = random.nextInt(10)
            numeroClave.append(digito)
        }

        return numeroClave.toString()
    }
}