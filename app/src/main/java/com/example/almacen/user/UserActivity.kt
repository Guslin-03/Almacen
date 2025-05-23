package com.example.almacen.user

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.almacen.R
import com.example.almacen.data.model.Usuario
import com.example.almacen.product.ProductActivity

class UserActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        // Datos de ejemplo
        val listaUsuarios = listOf(
            Usuario("Juan Pérez", R.drawable.user1),
            Usuario("María García", R.drawable.user2),
            Usuario("Admin Principal", R.drawable.user3, true, "admin123")
        )

        val recyclerView = findViewById<RecyclerView>(R.id.usuariosRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = UsuarioAdapter(listaUsuarios) { usuario ->
            manejarClicUsuario(usuario)
        }
    }

    private fun manejarClicUsuario(usuario: Usuario) {
        if (usuario.esAdmin) {
            mostrarDialogoContrasena(usuario)
        } else {
            irAPantallaProductos(usuario)
        }
    }

    private fun mostrarDialogoContrasena(usuario: Usuario) {
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

    private fun irAPantallaProductos(usuario: Usuario) {
        val intent = Intent(this, ProductActivity::class.java).apply {
            putExtra("nombre_usuario", usuario.nombre)
        }
        startActivity(intent)
    }
}