package com.example.appbancaria

class UsuariosClass {
    var correo: String = ""
    var nombre: String = ""
    var password: String = ""
    var numero_telefono: Long = 0
    var fecha_alta: String = ""

    constructor(correo: String, nombre: String, password: String, numero_telefono: Long, fecha_alta: String) {
        this.correo = correo
        this.nombre = nombre
        this.password = password
        this.numero_telefono = numero_telefono
        this.fecha_alta = fecha_alta
    }
}