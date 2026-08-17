package com.example.appbancaria

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast

class MainRetiroSinTarjeta2 : AppCompatActivity() {
    private var saldoMiCuenta: Double = 0.0
    private var numeroMiCuenta: String = ""
    lateinit var edtImporte: EditText
    lateinit var edtMotivo: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_retiro_sin_tarjeta2)

        val correo = intent.getStringExtra("correo")

        val btnCancelar = findViewById<Button>(R.id.btnCancelar)
        val text1 = findViewById<TextView>(R.id.textView14)
        edtMotivo = findViewById(R.id.edtMotivo)
        val btnFinalizar = findViewById<Button>(R.id.btnFinalizar)

        btnCancelar.setOnClickListener {
            val intent = Intent(this, MainRetiroSinTarjeta::class.java)
            intent.putExtra("correo", correo)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
            finish()
        }

        edtImporte = findViewById(R.id.edtImporte)
        val importe = intent.getDoubleExtra("importe",0.0)
        edtImporte.setText("$importe")

        edtMotivo = findViewById(R.id.edtMotivo)

        var listMisCuentas: ListView = findViewById(R.id.listMisCuentas)
        var sqlManager = SQLManager(this)
        var arrayList = sqlManager.listCuentasAll(this)
        var adapterMisCuentas = correo?.let { adapterMisCuentas(this, arrayList, it) }
        listMisCuentas.adapter = adapterMisCuentas

        listMisCuentas.setOnItemClickListener { parent, view, position, id ->
            text1.visibility = View.VISIBLE
            edtMotivo.visibility = View.VISIBLE
            btnFinalizar.visibility = View.VISIBLE
            val cuentaOrigen = parent.adapter.getItem(position) as CuentasClass
            saldoMiCuenta = cuentaOrigen.saldo_inicial
            numeroMiCuenta = cuentaOrigen.numero_cuenta.toString()
        }

        btnFinalizar.setOnClickListener{
            if (testDataMotivo()){
                val importeValido = edtImporte.text.toString().toDoubleOrNull()
                if(numeroMiCuenta.isNotBlank() && importeValido != null && importeValido > 0 && saldoMiCuenta >= importeValido){
                    val intent = Intent(this, MainFacturaSinRetiro::class.java)
                    val motivo = edtMotivo.text.toString()
                    intent.putExtra("saldo_mi_cuenta", saldoMiCuenta)
                    intent.putExtra("numero_mi_cuenta", numeroMiCuenta)
                    intent.putExtra("importe", importe)
                    intent.putExtra("motivo", motivo)
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

    fun testDataMotivo():Boolean{
        var response = true
        if (edtMotivo.text.isEmpty()){
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
