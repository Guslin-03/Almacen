package com.example.almacen.data.repository

import com.example.almacen.data.model.User
import com.example.almacen.utils.Resource

interface UserRepository {
    suspend fun getUsers(): Resource<List<User>>
}