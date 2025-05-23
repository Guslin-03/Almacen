package com.example.almacen.data.model

data class Product(
    val id: String,
    val nombre: String,
    val imagenUri: String, // Ahora guardamos la URI como String
    var cantidad: Int = 0,
    val precio: Double
)