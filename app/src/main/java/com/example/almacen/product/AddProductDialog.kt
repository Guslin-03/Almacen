package com.example.almacen.product

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import com.example.almacen.R
import com.example.almacen.data.model.Product
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class AddProductDialog(
    private val context: Context,
    private val takePictureLauncher: ActivityResultLauncher<Intent>,
    private val onProductAdded: (Product) -> Unit
) {
    companion object {
        var currentPhotoPath: String? = null

        fun handleCameraResult(activity: Activity) {
            currentPhotoPath?.let { path ->
                val dialog = (activity as? AddProductDialogInterface)?.getCurrentDialog()
                dialog?.updateImagePreview(Uri.parse(path))
            }
        }
    }

    private lateinit var dialogView: View
    private lateinit var imageView: ImageView
    private var currentDialog: AlertDialog? = null

    interface AddProductDialogInterface {
        fun getCurrentDialog(): AddProductDialog
        fun updateImagePreview(uri: Uri)
    }

    fun show() {
        dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_product, null)
        val etNombre = dialogView.findViewById<EditText>(R.id.etNombre)
        val etPrecio = dialogView.findViewById<EditText>(R.id.etPrecio)
        imageView = dialogView.findViewById(R.id.imgPreview)

        currentDialog = AlertDialog.Builder(context)
            .setTitle("Añadir nuevo producto")
            .setView(dialogView)
            .setPositiveButton("Añadir") { _, _ ->
                val nombre = etNombre.text.toString()
                val precio = etPrecio.text.toString().toDoubleOrNull() ?: 0.0

                if (nombre.isNotEmpty() && precio > 0 && currentPhotoPath != null) {
                    val nuevoProducto = Product(
                        id = 0,
                        nombre = nombre,
                        imagenUri = currentPhotoPath!!,
                        habilitado = true,
                        precio = precio
                    )
                    onProductAdded(nuevoProducto)
                } else {
                    Toast.makeText(context, "Datos inválidos o falta imagen", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()

        imageView.setOnClickListener {
            dispatchTakePictureIntent()
        }
    }

    fun updateImagePreview(uri: Uri) {
        imageView.setImageURI(uri)
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_${timeStamp}_"
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        return File.createTempFile(
            imageFileName,
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(context.packageManager)?.also {
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    Toast.makeText(context, "Error creando archivo", Toast.LENGTH_SHORT).show()
                    null
                }

                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    takePictureLauncher.launch(takePictureIntent)
                }
            }
        }
    }
}