package com.example.appbancaria

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast

class MainRecargaTiempoAire : AppCompatActivity() {
    private var saldoMiCuenta: Double = 0.0
    private var numeroMiCuenta: String = ""
    private var numeroTelefono: Long = 0
    lateinit var edtNumeroTelefono: EditText
    lateinit var telefonia_spinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_recarga_tiempo_aire)

        val text1 = findViewById<TextView>(R.id.textView27)
        edtNumeroTelefono = findViewById(R.id.edtNumeroTelefono)
        val text2 = findViewById<TextView>(R.id.textView29)
        telefonia_spinner = findViewById(R.id.telefonia_spinner)

        val btnCancelar = findViewById<Button>(R.id.btnCancelar)
        val btnContinuar = findViewById<Button>(R.id.btnContinuar)

        val correo = intent.getStringExtra("correo")

        var listMisCuentas: ListView = findViewById(R.id.listMisCuentas)
        var sqlManager = SQLManager(this)
        var arrayList = sqlManager.listCuentasAll(this)
        var adapterMisCuentas = correo?.let { adapterMisCuentas(this, arrayList, it) }
        listMisCuentas.adapter = adapterMisCuentas

        var listNumeroTelefono: ListView = findViewById(R.id.listNumeroTelefono)
        var arrayList2 = sqlManager.listUsuariosAll(this)
        var adapterTelefono = correo?.let { adapterTelefono(this, arrayList2, it) }
        listNumeroTelefono.adapter = adapterTelefono

        listMisCuentas.setOnItemClickListener { parent, view, position, id ->
            text1.visibility = View.VISIBLE
            listNumeroTelefono.visibility = View.VISIBLE
            edtNumeroTelefono.visibility = View.VISIBLE
            text2.visibility = View.VISIBLE
            telefonia_spinner.visibility = View.VISIBLE
            btnContinuar.visibility = View.VISIBLE

            val cuentaOrigen = parent.adapter.getItem(position) as CuentasClass
            saldoMiCuenta = cuentaOrigen.saldo_inicial
            numeroMiCuenta = cuentaOrigen.numero_cuenta.toString()
        }

        listNumeroTelefono.setOnItemClickListener { parent, view, position, id ->
            val usuario = parent.adapter.getItem(position) as UsuariosClass
            numeroTelefono = usuario.numero_telefono
            edtNumeroTelefono.setText(numeroTelefono.toString())
            edtNumeroTelefono.setSelection(edtNumeroTelefono.text.length)
        }

        btnContinuar.setOnClickListener{
            if (numeroMiCuenta.isBlank() || numeroTelefono == 0L) {
                Toast.makeText(this, "Selecciona una cuenta y un teléfono", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val intent = Intent(this, MainRecargaTiempoAire2::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            intent.putExtra("numero_cuenta", numeroMiCuenta)
            intent.putExtra("numero_telefono", numeroTelefono)
            intent.putExtra("correo", correo)
            startActivity(intent)
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
