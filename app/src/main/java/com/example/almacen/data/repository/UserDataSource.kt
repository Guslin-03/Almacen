package com.example.almacen.data.repository

class UserDataSource: BaseDataSource(), UserRepository {

    override suspend fun getUsers() = getResult {
        RetrofitClient.apiInterface.getUsers()
    }


}