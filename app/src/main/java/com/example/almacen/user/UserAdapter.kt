package com.example.almacen.user

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.almacen.R
import com.example.almacen.data.model.Usuario

class UsuarioAdapter(
    private val usuarios: List<Usuario>,
    private val onUsuarioClick: (Usuario) -> Unit
) : RecyclerView.Adapter<UsuarioAdapter.UsuarioViewHolder>() {

    inner class UsuarioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val fotoPerfil: ImageView = itemView.findViewById(R.id.fotoPerfilImageView)
        private val nombre: TextView = itemView.findViewById(R.id.nombreTextView)
        private val adminBadge: TextView = itemView.findViewById(R.id.adminBadge)

        fun bind(usuario: Usuario) {
            // Configurar imagen de perfil
            fotoPerfil.setImageResource(usuario.fotoPerfil)

            // Configurar nombre
            nombre.text = usuario.nombre
            nombre.setTextColor(
                ContextCompat.getColor(
                    itemView.context,
                    if (usuario.esAdmin) R.color.colorAccent else R.color.primary_text
                )
            )

            // Mostrar u ocultar badge de admin
            adminBadge.visibility = if (usuario.esAdmin) View.VISIBLE else View.GONE

            // Cambiar fondo para admins
            itemView.background = ContextCompat.getDrawable(
                itemView.context,
                if (usuario.esAdmin) R.drawable.admin_item_background else R.drawable.normal_item_background
            )

            itemView.setOnClickListener { onUsuarioClick(usuario) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsuarioViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user, parent, false)
        return UsuarioViewHolder(view)
    }

    override fun onBindViewHolder(holder: UsuarioViewHolder, position: Int) {
        holder.bind(usuarios[position])
    }

    override fun getItemCount(): Int = usuarios.size
}