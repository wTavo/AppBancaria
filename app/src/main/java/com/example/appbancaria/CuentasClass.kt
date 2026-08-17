package com.example.appbancaria

class CuentasClass {
    var aux_correo: String = ""
    var numero_cuenta: Long = 0
    var saldo_inicial: Double = 0.0

    constructor(aux_correo: String, numero_cuenta: Long, saldo_inicial: Double) {
        this.aux_correo = aux_correo
        this.numero_cuenta = numero_cuenta
        this.saldo_inicial = saldo_inicial
    }
}