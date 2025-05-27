package com.example.almacen.data.model

import com.example.almacen.R

data class User(
    val id: Int,
    val email: String,
    val nombre: String,
    val apellido: String,
    val fotoPerfil: Int,
    val foto: String,
    val habilitado: Boolean,
    val tipoUser: String,
    val esAdmin: Boolean,
    val password: String? = null
) {
    constructor() : this(
        id = -1,
        email = "",
        nombre = "Sin nombre",
        apellido = "Sin apellido",
        fotoPerfil = R.drawable.user3,
        foto = "",
        habilitado = false,
        tipoUser = "usuario",
        esAdmin = false,
        password = null
    )
}