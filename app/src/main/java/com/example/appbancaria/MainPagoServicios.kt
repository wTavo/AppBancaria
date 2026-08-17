package com.example.appbancaria

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast

class MainPagoServicios : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_pago_servicios)

        val btnCancelar = findViewById<Button>(R.id.btnCancelar)
        val btnPagar = findViewById<Button>(R.id.btnPagar)
        val correo = intent.getStringExtra("correo")

        btnPagar.setOnClickListener {
            val manager = SQLManager(this)
            val cuenta = correo?.let { manager.listCuentasAll(this).firstOrNull { cuenta -> cuenta.aux_correo == it } }
            if (cuenta == null || cuenta.saldo_inicial < 100.0 || !manager.updateSaldo(this, cuenta.numero_cuenta.toString(), cuenta.saldo_inicial - 100.0)) {
                Toast.makeText(this, "Saldo insuficiente o cuenta inválida", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            Toast.makeText(this, "Pago realizado", Toast.LENGTH_SHORT).show()
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
