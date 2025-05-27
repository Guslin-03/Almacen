package com.example.almacen.data.repository

class ProductDataSource: BaseDataSource(), ProductRepository {

    override suspend fun getProducts() = getResult {
        RetrofitClient.apiInterface.getProducts()
    }

}