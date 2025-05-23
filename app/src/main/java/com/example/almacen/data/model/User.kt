package com.example.almacen.data.model

data class Usuario(
    val nombre: String,
    val fotoPerfil: Int,
    val esAdmin: Boolean = false,
    val password: String? = null // Solo para admins
)