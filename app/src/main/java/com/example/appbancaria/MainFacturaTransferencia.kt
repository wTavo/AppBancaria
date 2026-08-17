package com.example.appbancaria

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class MainFacturaTransferencia : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_factura_transferencia)

        val btnSalir = findViewById<Button>(R.id.btnContinuar)

        val numero_mi_cuenta = intent.getStringExtra("numero_mi_cuenta")
        val numero_cuenta = intent.getStringExtra("numero_cuenta")
        val importe = intent.getDoubleExtra("importe",0.0)
        val sqlManager = SQLManager(this)


        val correoMiCuenta = numero_mi_cuenta?.let { getCorreo(it) }
        val correoCuenta = numero_cuenta?.let { getCorreo(it) }

        val ultimosDigitos = correoCuenta?.let { getUltimosDigitos(it) }
        val tvNumeroCuenta = findViewById<TextView>(R.id.tvNumeroTelefono)
        tvNumeroCuenta.text = "·$ultimosDigitos"

        val saldoCuenta = numero_cuenta?.toLong()?.let { getSaldo(it) }
        val operacionRealizada = intent.getBooleanExtra("operacion_realizada", false)
        val saldoRealCuenta = if (operacionRealizada) saldoCuenta ?: 0.0 else saldoCuenta?.plus(importe) ?: 0.0
        val tvSaldo = findViewById<TextView>(R.id.tvSaldo)
        tvSaldo.text = "$$saldoRealCuenta"


        if (!operacionRealizada) {
            val realizada = numero_mi_cuenta != null && numero_cuenta != null &&
                sqlManager.transferir(numero_mi_cuenta, numero_cuenta, importe)
            if (!realizada) {
                Toast.makeText(this, "No se pudo realizar la transferencia", Toast.LENGTH_LONG).show()
                finish()
                return
            }
            intent.putExtra("operacion_realizada", true)
        }

        btnSalir.setOnClickListener{
            val intent = Intent(this, MainInicioCuenta::class.java)
            intent.putExtra("correo", correoMiCuenta)
            startActivity(intent)
            finish()
        }
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
