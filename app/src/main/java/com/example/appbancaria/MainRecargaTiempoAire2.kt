package com.example.appbancaria

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class MainRecargaTiempoAire2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_recarga_tiempo_aire2)

        val btnCancelar = findViewById<Button>(R.id.btnCancelar)
        val btnConfirmar = findViewById<Button>(R.id.btnConfirmar)
        val numeroCuenta = intent.getStringExtra("numero_cuenta")
        val numeroTelefono = intent.getLongExtra("numero_telefono", 0L)
        val correo = intent.getStringExtra("correo")
        findViewById<EditText>(R.id.edtNumeroTelefono2).setText(numeroTelefono.toString())
        findViewById<TextView>(R.id.tvNumeroCuenta2).text = numeroCuenta ?: "Sin cuenta"
        findViewById<TextView>(R.id.tvSaldo2).text = "Importe: $100"

        btnConfirmar.setOnClickListener {
            val saldo = numeroCuenta?.let { SQLManager(this).getSaldo(it) }
            if (saldo == null || saldo < 100.0 || !SQLManager(this).updateSaldo(this, numeroCuenta, saldo - 100.0)) {
                Toast.makeText(this, "Saldo insuficiente o cuenta inválida", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            Toast.makeText(this, "Recarga realizada", Toast.LENGTH_SHORT).show()
            finish()
        }

        btnCancelar.setOnClickListener {
            val intent = Intent(this, MainInicioCuenta::class.java)
            intent.putExtra("correo", correo)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
            finish()
        }
    }
}
