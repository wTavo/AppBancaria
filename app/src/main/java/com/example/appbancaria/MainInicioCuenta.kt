package com.example.appbancaria

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView
import android.widget.PopupMenu
import android.widget.TextView
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.Random

class MainInicioCuenta : AppCompatActivity() {
    private lateinit var listMisCuentas: ListView
    private var correoUsuario: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_inicio_cuenta)

        val navegacionView = findViewById<BottomNavigationView>(R.id.transferenciaView)

        correoUsuario = intent.getStringExtra("correo")
        val correo = correoUsuario

        val numero_cuenta = correo?.let { getNumeroCuenta(it) }

        listMisCuentas = findViewById(R.id.listMisCuentas)

        navegacionView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.transferir_item -> {
                    val intent = Intent(this, MainTransferencia::class.java)
                    intent.putExtra("correo", correo)
                    intent.putExtra("numero_cuenta", numero_cuenta)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    startActivity(intent)
                    true
                }

                R.id.retiro_sin_tarjeta_item -> {
                    val intent = Intent(this, MainRetiroSinTarjeta::class.java)
                    intent.putExtra("correo", correo)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    startActivity(intent)
                    true
                }

                R.id.mas_opciones_item -> {
                    showPopupMenu()
                    true
                }

                else -> false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        cargarCuentas()
    }

    override fun onNewIntent(newIntent: Intent) {
        super.onNewIntent(newIntent)
        setIntent(newIntent)
        correoUsuario = newIntent.getStringExtra("correo")
    }

    private fun cargarCuentas() {
        val correo = correoUsuario ?: return
        val cuentas = SQLManager(this).listCuentasAll(this)
        listMisCuentas.adapter = adapterMisCuentas(this, cuentas, correo)
    }

    fun getUltimosDigitos(correo: String): String {
        val sqlManager = SQLManager(this)
        val db = sqlManager.readableDatabase
        val query = "SELECT numero_cuenta FROM agregar_cuentas WHERE aux_correo = ?"
        val cursor = db.rawQuery(query, arrayOf(correo))
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

    private fun showPopupMenu() {
        val popupMenu = PopupMenu(this, findViewById(R.id.mas_opciones_item))
        popupMenu.inflate(R.menu.menu_desplegable)
        val correo = correoUsuario

        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.recarga_tiempo_aire_item -> {
                    val intent = Intent(this, MainRecargaTiempoAire::class.java)
                    intent.putExtra("correo", correo)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    startActivity(intent)
                    true
                }
                R.id.pago_servicios_item -> {
                    val intent = Intent(this, MainPagoServicios::class.java)
                    intent.putExtra("correo", correo)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    startActivity(intent)
                    true
                }
                R.id.cerrar_sesion_item -> {
                    val intent = Intent(this, MainInicioSesion::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    startActivity(intent)
                    finish()
                    true
                }

                else -> false
            }
        }

        popupMenu.show()
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
