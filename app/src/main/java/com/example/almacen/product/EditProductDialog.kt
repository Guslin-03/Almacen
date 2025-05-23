import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.EditText
import com.example.almacen.R
import com.example.almacen.data.model.Product

class EditProductDialog(
    context: Context,
    private val product: Product,
    private val onProductUpdated: (Product) -> Unit
) : AlertDialog(context) {

    private lateinit var etName: EditText
    private lateinit var etPrice: EditText

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_edit_product, null)
        setView(view)

        etName = view.findViewById(R.id.etName)
        etPrice = view.findViewById(R.id.etPrice)

        // Configurar valores iniciales
        etName.setText(product.nombre)
        etPrice.setText(product.precio.toString())

        // Configurar botones
        setButton(BUTTON_POSITIVE, "Guardar") { _, _ ->
            val updatedProduct = product.copy(
                nombre = etName.text.toString(),
                precio = etPrice.text.toString().toDoubleOrNull() ?: product.precio
            )
            onProductUpdated(updatedProduct)
        }

        setButton(BUTTON_NEGATIVE, "Cancelar") { _, _ -> dismiss() }

        setTitle("Editar Producto")
    }
}