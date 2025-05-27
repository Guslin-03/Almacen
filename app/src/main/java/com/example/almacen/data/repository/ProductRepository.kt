package com.example.almacen.data.repository

import com.example.almacen.data.model.Product
import com.example.almacen.utils.Resource

interface ProductRepository {

    suspend fun getProducts(): Resource<List<Product>>

}