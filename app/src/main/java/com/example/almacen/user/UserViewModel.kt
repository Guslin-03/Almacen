package com.example.almacen.user

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.almacen.data.model.User
import com.example.almacen.data.repository.UserRepository
import com.example.almacen.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserViewModel(private val userRepository: UserRepository,
                    private val context:Context) : ViewModel() {

    private val _users = MutableLiveData<Resource<List<User>>>()
    val users : LiveData<Resource<List<User>>> get() = _users

    private suspend fun users() : Resource<List<User>> {
        return withContext(Dispatchers.IO) {
            userRepository.getUsers()
        }
    }
    fun onUsers() {
        viewModelScope.launch {
            val response = users()
            _users.value = response
        }
    }

}
class UserViewModelFactory(
    private val userRepository:UserRepository,
    private val context: Context
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return UserViewModel(userRepository, context) as T
    }

}