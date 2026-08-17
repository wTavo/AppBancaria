package com.example.appbancaria

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class MainRetiroSinTarjeta : AppCompatActivity() {
    lateinit var edtImporte: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_retiro_sin_tarjeta)

        val btnCancelar = findViewById<Button>(R.id.btnCancelar)
        val correo = intent.getStringExtra("correo")

        btnCancelar.setOnClickListener {
            val intent = Intent(this, MainInicioCuenta::class.java)
            intent.putExtra("correo", correo)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
            finish()
        }

        edtImporte = findViewById(R.id.edtImporte)
        val btn100 = findViewById<Button>(R.id.btn100)
        val btn200 = findViewById<Button>(R.id.btn200)
        val btn500 = findViewById<Button>(R.id.btn500)
        val btn800 = findViewById<Button>(R.id.btn800)
        val btn1000 = findViewById<Button>(R.id.btn1000)
        val btn1500 = findViewById<Button>(R.id.btn1500)
        val btnContinuarImporte = findViewById<Button>(R.id.btnContinuarImporte)

        val saldo = correo?.let { getSaldo(it) }?.toDoubleOrNull() ?: 0.0

        btn100.setOnClickListener {
            edtImporte.setText("100")
        }

        btn200.setOnClickListener {
            edtImporte.setText("200")
        }

        btn500.setOnClickListener {
            edtImporte.setText("500")
        }

        btn800.setOnClickListener {
            edtImporte.setText("800")
        }

        btn1000.setOnClickListener {
            edtImporte.setText("1000")
        }

        btn1500.setOnClickListener {
            edtImporte.setText("1500")
        }

        btnContinuarImporte.setOnClickListener{
            if (testDataImporte()){
                val importe = edtImporte.text.toString().toDoubleOrNull()
                if(importe != null && importe > 0 && saldo >= importe){
                    val intent = Intent(this, MainRetiroSinTarjeta2::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    intent.putExtra("importe", importe)
                    intent.putExtra("correo", correo)
                    startActivity(intent)
                    finish()
                }else{
                    Toast.makeText(this,"No puedes ingresar un importe mayor al saldo disponible",
                        Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this,"El campo es obligatorio",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun testDataImporte():Boolean{
        var response = true
        val importe = edtImporte.text.toString().toDoubleOrNull()
        if (importe == null || importe <= 0){
            response = false
        }
        return response
    }

    fun getSaldo(aux_correo: String): String {
        val sqlManager = SQLManager(this)
        val db = sqlManager.readableDatabase
        val query = "SELECT saldo_inicial FROM agregar_cuentas WHERE aux_correo = ?"
        val cursor = db.rawQuery(query, arrayOf(aux_correo))
        var saldo = ""

        if (cursor.moveToFirst()) {
            saldo = cursor.getString(cursor.getColumnIndexOrThrow("saldo_inicial"))
        }
        cursor.close()
        db.close()
        return saldo
    }
}
