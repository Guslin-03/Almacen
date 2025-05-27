package com.example.almacen.product

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.almacen.data.model.Product
import com.example.almacen.data.repository.ProductRepository
import com.example.almacen.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProductViewModel(private val productRepository: ProductRepository,
                       private val context: Context
) : ViewModel() {

    private val _products = MutableLiveData<Resource<List<Product>>>()
    val products : LiveData<Resource<List<Product>>> get() = _products

    private suspend fun products() : Resource<List<Product>> {
        return withContext(Dispatchers.IO) {
            productRepository.getProducts()
        }
    }
    fun onProducts() {
        viewModelScope.launch {
            val response = products()
            _products.value = response
        }
    }

}
class RoomProductViewModelFactory(
    private val productRepository: ProductRepository,
    private val context: Context
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return ProductViewModel(productRepository, context) as T
    }

}