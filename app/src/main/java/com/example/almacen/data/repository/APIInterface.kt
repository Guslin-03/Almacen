package com.example.almacen.data.repository

import com.example.almacen.data.model.Product
import com.example.almacen.data.model.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST


interface APIInterface {

    //@POST("login")
    //suspend fun login(@Body authRequest: AuthRequest) : Response<LoginUser>

    //    @POST("logout")
    //    suspend fun logout() : Response<Void>

    @GET("users")
    suspend fun getUsers(): Response<List<User>>

    @GET("products/findAll")
    suspend fun getProducts(): Response<List<Product>>


}