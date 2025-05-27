package com.example.almacen.user

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.almacen.R
import com.example.almacen.data.model.User
import com.example.almacen.data.repository.UserDataSource
import com.example.almacen.databinding.ActivityUserBinding
import com.example.almacen.product.ProductActivity
import com.example.almacen.utils.Resource

class UserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserBinding
    private val userRepository = UserDataSource()
    private val viewModel: UserViewModel by viewModels { UserViewModelFactory(userRepository, this) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        viewModel.onUsers()

        viewModel.users.observe(this) {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    val userResource = viewModel.users.value
                    if (userResource != null && userResource.status == Resource.Status.SUCCESS) {
                        val listaUsuarios = userResource.data
                        val recyclerView = findViewById<RecyclerView>(R.id.usuariosRecyclerView)
                        recyclerView.layoutManager = LinearLayoutManager(this)
                        recyclerView.adapter =
                            listaUsuarios?.let { it1 ->
                                UsuarioAdapter(it1) { usuario ->
                                    manejarClicUsuario(usuario)
                                }
                            }
                    }
                }
                Resource.Status.ERROR -> {
                    Toast.makeText(this, "Error", Toast.LENGTH_LONG).show()
                }
                Resource.Status.LOADING -> {
                }
            }
        }
    }

    private fun manejarClicUsuario(usuario: User) {
        if (usuario.esAdmin) {
            mostrarDialogoContrasena(usuario)
        } else {
            irAPantallaProductos(usuario)
        }
    }

    private fun mostrarDialogoContrasena(usuario: User) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_password, null)
        val editTextContrasena = dialogView.findViewById<EditText>(R.id.editTextContrasena)

        AlertDialog.Builder(this)
            .setTitle("Acceso Admin")
            .setMessage("Ingrese la contraseña para ${usuario.nombre}")
            .setView(dialogView)
            .setPositiveButton("Aceptar") { _, _ ->
                val contrasenaIngresada = editTextContrasena.text.toString()
                if (contrasenaIngresada == usuario.password) {
                    irAPantallaProductos(usuario)
                } else {
                    Toast.makeText(this, "Contraseña incorrecta", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun irAPantallaProductos(usuario: User) {
        val intent = Intent(this, ProductActivity::class.java).apply {
            putExtra("nombre_usuario", usuario.nombre)
        }
        startActivity(intent)
    }
}