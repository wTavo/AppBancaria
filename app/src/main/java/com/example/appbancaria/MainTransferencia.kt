package com.example.appbancaria

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast

class MainTransferencia : AppCompatActivity() {
    lateinit var edtImporte: EditText
    lateinit var edtMotivo: EditText

    private var saldoMiCuenta: Double = 0.0
    private var numeroMiCuenta: String = ""
    private var saldoCuenta: Double = 0.0
    private var numeroCuenta: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_transferencia)

        val btnCancelar = findViewById<Button>(R.id.btnCancelar)
        val text1 = findViewById<TextView>(R.id.textView8)
        val text2 = findViewById<TextView>(R.id.textView9)
        edtImporte = findViewById(R.id.edtImporte)
        val btnContinuarImporte = findViewById<Button>(R.id.btnContinuarImporte)
        val text3 = findViewById<TextView>(R.id.textView1)
        edtMotivo = findViewById(R.id.edtMotivo)
        val btnFinalizar = findViewById<Button>(R.id.btnFinalizar)

        val correo = intent.getStringExtra("correo")



        var listMisCuentas: ListView = findViewById(R.id.listMisCuentas)
        var sqlManager = SQLManager(this)
        var arrayList = sqlManager.listCuentasAll(this)
        var adapterMisCuentas = correo?.let { adapterMisCuentas(this, arrayList, it) }
        listMisCuentas.adapter = adapterMisCuentas

        var listCuentas: ListView = findViewById(R.id.listNumeroTelefono)

        listMisCuentas.setOnItemClickListener { parent, view, position, id ->
            text1.visibility = View.VISIBLE
            listCuentas.visibility = View.VISIBLE
            val cuentaOrigen = parent.adapter.getItem(position) as CuentasClass
            saldoMiCuenta = cuentaOrigen.saldo_inicial
            numeroMiCuenta = cuentaOrigen.numero_cuenta.toString()
            val numero = numeroMiCuenta.toLong()
            var adapterCuentas = numero?.let { adapterCuentas(this, arrayList, it) }
            listCuentas.adapter = adapterCuentas
        }

        listCuentas.setOnItemClickListener { parent, view, position, id ->
            text2.visibility = View.VISIBLE
            edtImporte.visibility = View.VISIBLE
            btnContinuarImporte.visibility = View.VISIBLE
            val cuentaDestino = parent.adapter.getItem(position) as CuentasClass
            numeroCuenta = cuentaDestino.numero_cuenta.toString()
            saldoCuenta = cuentaDestino.saldo_inicial
        }

        btnContinuarImporte.setOnClickListener{
            if (testDataImporte()){
                val importe = edtImporte.text.toString().toDoubleOrNull()
                if(importe != null && numeroMiCuenta.isNotBlank() && numeroCuenta != numeroMiCuenta && saldoMiCuenta >= importe){
                    text3.visibility = View.VISIBLE
                    edtMotivo.visibility = View.VISIBLE
                    btnFinalizar.visibility = View.VISIBLE
                }else{
                    Toast.makeText(this,"No puedes ingresar un importe mayor al saldo disponible",
                        Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this,"El campo es obligatorio",
                    Toast.LENGTH_SHORT).show()
            }
        }

        btnFinalizar.setOnClickListener{
            if (testDataMotivo()){
                val importe = edtImporte.text.toString().toDoubleOrNull()
                if(importe != null && numeroMiCuenta.isNotBlank() && numeroCuenta != numeroMiCuenta && saldoMiCuenta >= importe){
                    val intent = Intent(this, MainFacturaTransferencia::class.java)
                    intent.putExtra("saldo_mi_cuenta", saldoMiCuenta)
                    intent.putExtra("numero_mi_cuenta", numeroMiCuenta)
                    intent.putExtra("saldo_cuenta", saldoCuenta)
                    intent.putExtra("numero_cuenta", numeroCuenta)
                    intent.putExtra("importe", importe)

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

        btnCancelar.setOnClickListener {
            val intent = Intent(this, MainInicioCuenta::class.java)
            intent.putExtra("correo", correo)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
            finish()
        }
    }

    fun testDataImporte():Boolean{
        var response = true
        if (edtImporte.text.toString().toDoubleOrNull()?.let { it > 0 } != true){
            response = false
        }
        return response
    }

    fun testDataMotivo():Boolean{
        var response = true
        if (edtImporte.text.toString().toDoubleOrNull()?.let { it > 0 } != true || edtMotivo.text.isEmpty()){
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

    fun getNumeroCuenta(aux_correo: String): Long {
        val sqlManager = SQLManager(this)
        val db = sqlManager.readableDatabase
        val query = "SELECT numero_cuenta FROM agregar_cuentas WHERE aux_correo = ?"
        val cursor = db.rawQuery(query, arrayOf(aux_correo))
        var numero: Long = 0

        if (cursor.moveToFirst()) {
            numero = cursor.getLong(cursor.getColumnIndexOrThrow("numero_cuenta"))
        }
        cursor.close()
        db.close()
        return numero
    }
}
