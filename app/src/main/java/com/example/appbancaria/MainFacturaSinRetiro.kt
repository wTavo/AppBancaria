package com.example.appbancaria

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class MainFacturaSinRetiro : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_factura_sin_retiro)

        val numero_mi_cuenta = intent.getStringExtra("numero_mi_cuenta")
        val importe = intent.getDoubleExtra("importe",0.0)
        val motivo = intent.getStringExtra("motivo")
        val sqlManager = SQLManager(this)
        val btnContinuar = findViewById<Button>(R.id.btnContinuar)

        val correoMiCuenta = numero_mi_cuenta?.let { getCorreo(it) }

        val ultimosDigitos = correoMiCuenta?.let { getUltimosDigitos(it) }
        val tvNumeroCuenta = findViewById<TextView>(R.id.tvNumeroTelefono)
        tvNumeroCuenta.text = "·$ultimosDigitos"

        val tvImporte = findViewById<TextView>(R.id.tvImporte)
        val tvImporte2 = findViewById<TextView>(R.id.tvImporte2)
        tvImporte.text = "$$importe"
        tvImporte2.text = "$$importe"

        val tvMotivo = findViewById<TextView>(R.id.tvMotivo)
        tvMotivo.text = "$motivo"

        if (!intent.getBooleanExtra("operacion_realizada", false)) {
            val saldoMiCuenta = numero_mi_cuenta?.toLong()?.let { getSaldo(it) } ?: 0.0
            if (importe <= 0 || saldoMiCuenta < importe || numero_mi_cuenta == null ||
                !sqlManager.updateSaldo(this, numero_mi_cuenta, saldoMiCuenta - importe)) {
                Toast.makeText(this, "No se pudo realizar el retiro", Toast.LENGTH_LONG).show()
                finish()
                return
            }
            intent.putExtra("operacion_realizada", true)
        }

        btnContinuar.setOnClickListener{
            val intent = Intent(this, MainFacturaSinRetiro2 ::class.java)
            intent.putExtra("correo", correoMiCuenta)
            intent.putExtra("importe", importe)
            startActivity(intent)
            finish()
        }
    }

    fun getCorreo(numero_cuenta: String): String {
        val sqlManager = SQLManager(this)
        val db = sqlManager.readableDatabase
        val query = "SELECT aux_correo FROM agregar_cuentas WHERE numero_cuenta = ?"
        val cursor = db.rawQuery(query, arrayOf(numero_cuenta))
        var gCorreo = ""

        if (cursor.moveToFirst()) {
            gCorreo = cursor.getString(cursor.getColumnIndexOrThrow("aux_correo"))
        }

        cursor.close()
        db.close()
        return gCorreo
    }

    fun getUltimosDigitos(aux_correo: String): String {
        val sqlManager = SQLManager(this)
        val db = sqlManager.readableDatabase
        val query = "SELECT numero_cuenta FROM agregar_cuentas WHERE aux_correo = ?"
        val cursor = db.rawQuery(query, arrayOf(aux_correo))
        var ultimosDigitos = ""

        if (cursor.moveToFirst()) {
            val numero = cursor.getString(cursor.getColumnIndexOrThrow("numero_cuenta"))
            if (numero.length >= 4) {
                ultimosDigitos = numero.substring(numero.length - 4)
            }
        }

        cursor.close()
        db.close()
        return ultimosDigitos
    }

    fun getSaldo(numero_cuenta: Long): Double {
        val sqlManager = SQLManager(this)
        val db = sqlManager.readableDatabase
        val query = "SELECT saldo_inicial FROM agregar_cuentas WHERE numero_cuenta = ?"
        val cursor = db.rawQuery(query, arrayOf(numero_cuenta.toString()))
        var saldo = 0.0

        if (cursor.moveToFirst()) {
            saldo = cursor.getDouble(cursor.getColumnIndexOrThrow("saldo_inicial"))
        }
        cursor.close()
        db.close()
        return saldo
    }
}
