package com.example.almacen.data.model

data class Product(
    val id: Int,
    val nombre: String,
    val imagenUri: String,
    val habilitado: Boolean,
    var cantidad: Int = 0,
    val precio: Double
)