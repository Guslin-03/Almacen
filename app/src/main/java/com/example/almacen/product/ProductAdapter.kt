package com.example.almacen.product

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.almacen.R
import com.example.almacen.data.model.Product
import java.util.Locale

class ProductAdapter(
    private var originalProducts: List<Product>,
    private val onCantidadChanged: (Int, Int) -> Unit,
    private val onAddProductClick: () -> Unit,
    private val onProductSelected: (Product) -> Unit // Nueva callback para selección
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {

    private var filteredProducts: List<Product> = originalProducts
    private var showAddButton: Boolean = true
    private companion object {
        const val VIEW_TYPE_ADD_BUTTON = 0
        const val VIEW_TYPE_PRODUCT = 1
    }

    abstract class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    inner class ProductoViewHolder(itemView: View) : BaseViewHolder(itemView) {
        val imgProducto: ImageView = itemView.findViewById(R.id.imgProducto)
        val tvNombre: TextView = itemView.findViewById(R.id.tvNombre)
        val tvPrecio: TextView = itemView.findViewById(R.id.tvPrecio)
        val tvCantidad: TextView = itemView.findViewById(R.id.tvCantidad)
        val btnMas: Button = itemView.findViewById(R.id.btnMas)
        val btnMenos: Button = itemView.findViewById(R.id.btnMenos)

        fun bind(producto: Product, position: Int) {
            tvNombre.text = producto.nombre
            tvPrecio.text = itemView.context.getString(R.string.precio_formato, producto.precio)
            tvCantidad.text = producto.cantidad.toString()

            btnMas.setOnClickListener {
                val nuevaCantidad = producto.cantidad + 1
                producto.cantidad = nuevaCantidad
                tvCantidad.text = nuevaCantidad.toString()
                onCantidadChanged(position, nuevaCantidad)
            }

            btnMenos.setOnClickListener {
                if (producto.cantidad > 0) {
                    val nuevaCantidad = producto.cantidad - 1
                    producto.cantidad = nuevaCantidad
                    tvCantidad.text = nuevaCantidad.toString()
                    onCantidadChanged(position, nuevaCantidad)
                }
            }

            itemView.setOnClickListener {
                onProductSelected(producto) // Notificar selección
            }
        }
    }

    inner class AddButtonViewHolder(itemView: View) : BaseViewHolder(itemView) {
        init {
            itemView.findViewById<Button>(R.id.btnAddProduct).setOnClickListener {
                onAddProductClick()
            }
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val results = FilterResults()
                val filteredList = mutableListOf<Product>()

                if (constraint.isNullOrBlank()) {
                    filteredList.addAll(originalProducts)
                    showAddButton = true
                } else {
                    val filterPattern = constraint.toString().lowercase(Locale.getDefault()).trim()
                    for (product in originalProducts) {
                        if (product.nombre.lowercase(Locale.getDefault()).contains(filterPattern) ||
                            product.precio.toString().contains(filterPattern)) {
                            filteredList.add(product)
                        }
                    }
                    // Ocultar botón de añadir cuando hay texto de búsqueda
                    showAddButton = false
                }

                results.values = filteredList
                results.count = filteredList.size + if (showAddButton) 1 else 0
                return results
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredProducts = results?.values as? List<Product> ?: emptyList()
                notifyDataSetChanged()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (showAddButton && position == 0) VIEW_TYPE_ADD_BUTTON else VIEW_TYPE_PRODUCT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_ADD_BUTTON -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_add_product, parent, false)
                AddButtonViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_product, parent, false)
                ProductoViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ProductoViewHolder -> {
                val productPosition = if (showAddButton) position - 1 else position
                val producto = filteredProducts[productPosition]
                holder.bind(producto, productPosition)
                val uri = Uri.parse(producto.imagenUri)
                holder.imgProducto.setImageURI(uri)
            }
            // No necesitamos hacer nada para el AddButtonViewHolder
        }

    }

    override fun getItemCount(): Int = filteredProducts.size + if (showAddButton) 1 else 0

    fun updateProducts(newProducts: List<Product>) {
        originalProducts = newProducts
        filteredProducts = newProducts
        showAddButton = true
        notifyDataSetChanged()
    }
}