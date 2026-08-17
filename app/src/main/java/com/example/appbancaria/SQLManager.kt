package com.example.appbancaria

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/** Acceso local a los datos del ejercicio. */
class SQLManager(context: Context) : SQLiteOpenHelper(context, "Banco.db", null, 2) {
    private val appContext = context.applicationContext
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE usuarios (correo TEXT PRIMARY KEY, nombre TEXT NOT NULL, password TEXT NOT NULL, numero_telefono INTEGER UNIQUE NOT NULL, fecha_alta TEXT NOT NULL)")
        db.execSQL("CREATE TABLE agregar_cuentas (numero_cuenta INTEGER PRIMARY KEY, aux_correo TEXT NOT NULL, saldo_inicial REAL NOT NULL CHECK(saldo_inicial >= 0), FOREIGN KEY(aux_correo) REFERENCES usuarios(correo))")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // El prototipo original no tenía restricciones. Recrear la base evita conservar datos inconsistentes.
        if (oldVersion < 2) {
            db.execSQL("DROP TABLE IF EXISTS agregar_cuentas")
            db.execSQL("DROP TABLE IF EXISTS usuarios")
            onCreate(db)
        }
    }

    fun controlUsuariosCorreo(context: Context, correo: String) = exists("usuarios", "correo", correo)
    fun controlUsuariosTelefono(context: Context, numeroTelefono: Long) = exists("usuarios", "numero_telefono", numeroTelefono.toString())
    fun controlCuentas(context: Context, numeroCuenta: Long) = exists("agregar_cuentas", "numero_cuenta", numeroCuenta.toString())

    private fun exists(table: String, column: String, value: String): Boolean {
        val cursor = readableDatabase.query(table, arrayOf(column), "$column = ?", arrayOf(value), null, null, null, "1")
        return cursor.use { it.moveToFirst() }
    }

    fun listUsuariosAll(context: Context): ArrayList<UsuariosClass> {
        val result = ArrayList<UsuariosClass>()
        val cursor = readableDatabase.query("usuarios", arrayOf("correo", "nombre", "password", "numero_telefono", "fecha_alta"), null, null, null, null, "numero_telefono")
        cursor.use {
            while (it.moveToNext()) result.add(UsuariosClass(it.getString(0), it.getString(1), it.getString(2), it.getLong(3), it.getString(4)))
        }
        return result
    }

    fun listCuentasAll(context: Context): ArrayList<CuentasClass> {
        val result = ArrayList<CuentasClass>()
        val cursor = readableDatabase.query("agregar_cuentas", arrayOf("aux_correo", "numero_cuenta", "saldo_inicial"), null, null, null, null, "numero_cuenta")
        cursor.use {
            while (it.moveToNext()) result.add(CuentasClass(it.getString(0), it.getLong(1), it.getDouble(2)))
        }
        return result
    }

    fun addUsuarios(context: Context, datos: UsuariosClass): Boolean {
        val values = ContentValues().apply {
            put("correo", datos.correo.trim())
            put("nombre", datos.nombre.trim())
            put("password", PasswordUtils.hash(datos.password))
            put("numero_telefono", datos.numero_telefono)
            put("fecha_alta", datos.fecha_alta)
        }
        return writableDatabase.insert("usuarios", null, values) != -1L
    }

    fun addCuentas(context: Context, datos: CuentasClass): Boolean {
        val values = ContentValues().apply {
            put("aux_correo", datos.aux_correo.trim())
            put("numero_cuenta", datos.numero_cuenta)
            put("saldo_inicial", datos.saldo_inicial)
        }
        return writableDatabase.insert("agregar_cuentas", null, values) != -1L
    }

    fun registrarUsuarioConCuentas(usuario: UsuariosClass, primera: CuentasClass, segunda: CuentasClass): Boolean {
        val db = writableDatabase
        db.beginTransaction()
        return try {
            val usuarioValues = ContentValues().apply {
                put("correo", usuario.correo.trim())
                put("nombre", usuario.nombre.trim())
                put("password", PasswordUtils.hash(usuario.password))
                put("numero_telefono", usuario.numero_telefono)
                put("fecha_alta", usuario.fecha_alta)
            }
            val cuentaValues = { cuenta: CuentasClass -> ContentValues().apply {
                put("numero_cuenta", cuenta.numero_cuenta)
                put("aux_correo", cuenta.aux_correo.trim())
                put("saldo_inicial", cuenta.saldo_inicial)
            } }
            if (db.insert("usuarios", null, usuarioValues) == -1L) return false
            if (db.insert("agregar_cuentas", null, cuentaValues(primera)) == -1L) return false
            if (db.insert("agregar_cuentas", null, cuentaValues(segunda)) == -1L) return false
            db.setTransactionSuccessful()
            true
        } finally {
            db.endTransaction()
        }
    }

    fun autenticar(correo: String, password: String): Boolean {
        val cursor = readableDatabase.query("usuarios", arrayOf("password"), "correo = ?", arrayOf(correo.trim()), null, null, null)
        return cursor.use { it.moveToFirst() && PasswordUtils.matches(password, it.getString(0)) }
    }

    fun getSaldo(numeroCuenta: String): Double? {
        val cursor = readableDatabase.query("agregar_cuentas", arrayOf("saldo_inicial"), "numero_cuenta = ?", arrayOf(numeroCuenta), null, null, null)
        return cursor.use { if (it.moveToFirst()) it.getDouble(0) else null }
    }

    fun updateSaldo(context: Context, numeroCuenta: String?, nuevoSaldo: Double): Boolean {
        if (numeroCuenta.isNullOrBlank() || nuevoSaldo < 0) return false
        val values = ContentValues().apply { put("saldo_inicial", nuevoSaldo) }
        return writableDatabase.update("agregar_cuentas", values, "numero_cuenta = ?", arrayOf(numeroCuenta)) == 1
    }

    fun transferir(numeroOrigen: String, numeroDestino: String, importe: Double): Boolean {
        if (numeroOrigen == numeroDestino || importe <= 0) return false
        val db = writableDatabase
        db.beginTransaction()
        return try {
            val origen = getSaldo(numeroOrigen) ?: return false
            val destino = getSaldo(numeroDestino) ?: return false
            if (origen < importe) return false
            if (!updateSaldo(appContext, numeroOrigen, origen - importe) || !updateSaldo(appContext, numeroDestino, destino + importe)) return false
            db.setTransactionSuccessful()
            true
        } finally {
            db.endTransaction()
        }
    }
}
