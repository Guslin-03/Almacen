    package com.example.almacen.product

    import EditProductDialog
    import android.content.pm.PackageManager
    import android.os.Bundle
    import android.widget.TextView
    import android.widget.Toast
    import androidx.activity.result.contract.ActivityResultContracts
    import androidx.appcompat.app.AlertDialog
    import androidx.appcompat.app.AppCompatActivity
    import androidx.core.app.ActivityCompat
    import androidx.core.content.ContextCompat
    import androidx.recyclerview.widget.GridLayoutManager
    import androidx.recyclerview.widget.RecyclerView
    import com.example.almacen.R
    import com.example.almacen.data.model.Product
    import android.Manifest
    import android.content.Intent
    import android.net.Uri
    import android.view.MenuItem
    import android.view.WindowManager
    import androidx.activity.result.ActivityResultLauncher
    import androidx.appcompat.widget.SearchView
    import androidx.appcompat.widget.Toolbar
    import androidx.core.view.GravityCompat
    import androidx.drawerlayout.widget.DrawerLayout
    import com.google.android.material.navigation.NavigationView

    class ProductActivity : AppCompatActivity(), AddProductDialog.AddProductDialogInterface {
        private lateinit var drawerLayout: DrawerLayout
        private lateinit var takePictureLauncher: ActivityResultLauncher<Intent>
        private var currentAddProductDialog: AddProductDialog? = null
        private lateinit var adapter: ProductAdapter
        private lateinit var navigationView: NavigationView
        private val productos = mutableListOf<Product>()
        private val isAdmin: Boolean by lazy { checkIfUserIsAdmin() } // Implementa esta función

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_product)

            // Configuración del Drawer
            drawerLayout = findViewById(R.id.drawer_layout)
            navigationView = findViewById(R.id.nav_view)

            // Configurar Toolbar
            val toolbar = findViewById<Toolbar>(R.id.toolbar)
            setSupportActionBar(toolbar)
            supportActionBar?.apply {
                setDisplayHomeAsUpEnabled(true)
                setHomeAsUpIndicator(R.drawable.pan_almacen)
            }

            // Ocultar sección privada si no es admin
            navigationView.menu.setGroupVisible(R.id.group_private, isAdmin)

            // Manejar selección de items del menú
            navigationView.setNavigationItemSelectedListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.nav_edit_product -> {
                        if (selectedProduct == null) {
                            Toast.makeText(this, "Selecciona un producto primero", Toast.LENGTH_SHORT).show()
                        } else {
                            editSelectedProduct()
                        }
                        true
                    }
                    R.id.nav_delete_product -> {
                        if (selectedProduct == null) {
                            Toast.makeText(this, "Selecciona un producto primero", Toast.LENGTH_SHORT).show()
                        } else {
                            deleteSelectedProduct()
                        }
                        true
                    }
                    R.id.nav_admin_settings -> {
                        if (isAdmin) openAdminSettings() else showAdminRestricted()
                        true
                    }
                    R.id.nav_user_management -> {
                        if (isAdmin) openUserManagement() else showAdminRestricted()
                        true
                    }
                    else -> false
                }.also {
                    drawerLayout.closeDrawer(GravityCompat.START)
                }
            }

            takePictureLauncher = registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) { result ->
                if (result.resultCode == RESULT_OK) {
                    AddProductDialog.handleCameraResult(this)
                }
            }

            // Configuración correcta del SearchView
            val searchView = findViewById<SearchView>(R.id.searchView)
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    adapter.filter.filter(newText)
                    return true
                }
            })

            // Initialize RecyclerView
            val recyclerView = findViewById<RecyclerView>(R.id.rvProductos)
            recyclerView.layoutManager = GridLayoutManager(this, 2)
            recyclerView.addItemDecoration(GridSpacingItemDecoration(2, 16, true))

            // Load products
            inicializarProductos()

            // Initialize adapter
            adapter = ProductAdapter(
                originalProducts = productos,
                onCantidadChanged = { position, newQuantity ->
                    productos[position].cantidad = newQuantity
                },
                onAddProductClick = { checkCameraPermission() },
                onProductSelected = { product ->
                    selectedProduct = product
                    Toast.makeText(this, "${product.nombre} seleccionado", Toast.LENGTH_SHORT).show()
                }
            )

            recyclerView.adapter = adapter

        }

        private fun showAdminRestricted() {
            Toast.makeText(this, "Acceso restringido a administradores", Toast.LENGTH_SHORT).show()
        }

        private var selectedProduct: Product? = null
            set(value) {
                field = value
                updateSelectionUI()
                // Puedes añadir más lógica aquí si es necesario
                if (value != null) {
                    Toast.makeText(this, "${value.nombre} seleccionado", Toast.LENGTH_SHORT).show()
                }
            }

        private fun updateSelectionUI() {
            val selectionText = findViewById<TextView>(R.id.tvSelectedProduct)
            selectionText.text = selectedProduct?.nombre ?: "Ningún producto seleccionado"
        }

        private fun editSelectedProduct() {
            selectedProduct?.let { product ->
                val dialog = EditProductDialog(this, product) { updatedProduct ->
                    // Actualizar producto en la lista
                    val index = productos.indexOfFirst { it.id == updatedProduct.id }
                    if (index != -1) {
                        productos[index] = updatedProduct
                        adapter.updateProducts(productos)
                        selectedProduct = updatedProduct // Actualizar producto seleccionado
                    }
                }
                dialog.show() // Mostrar diálogo

                // Forzar el enfoque y mostrar teclado
                dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
            } ?: run {
                Toast.makeText(this, "Selecciona un producto primero", Toast.LENGTH_SHORT).show()
            }
        }

        private fun deleteSelectedProduct() {
            selectedProduct?.let { product ->
                AlertDialog.Builder(this)
                    .setTitle("Confirmar borrado")
                    .setMessage("¿Estás seguro de borrar ${product.nombre}?")
                    .setPositiveButton("Borrar") { _, _ ->
                        productos.remove(product)
                        adapter.updateProducts(productos)
                        selectedProduct = null
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            } ?: run {
                Toast.makeText(this, "Selecciona un producto primero", Toast.LENGTH_SHORT).show()
            }
        }

        // Funciones para admin
        private fun openAdminSettings() { /* ... */ }
        private fun openUserManagement() { /* ... */ }

        override fun onOptionsItemSelected(item: MenuItem): Boolean {
            return when (item.itemId) {
                android.R.id.home -> {
                    drawerLayout.openDrawer(GravityCompat.START)
                    true
                }
                else -> super.onOptionsItemSelected(item)
            }
        }

        private fun checkIfUserIsAdmin(): Boolean {
            // Implementa tu lógica para verificar si el usuario es admin
            return false // o true según corresponda
        }

        // Mover el launcher a nivel de actividad
        private fun mostrarDialogoAgregarProducto() {
            // Verifica que el launcher esté inicializado
            if (!::takePictureLauncher.isInitialized) {
                Toast.makeText(this, "Error al inicializar la cámara", Toast.LENGTH_SHORT).show()
                return
            }

            currentAddProductDialog = AddProductDialog(this, takePictureLauncher) { nuevoProducto ->
                productos.add(nuevoProducto)
                adapter.updateProducts(productos)
                currentAddProductDialog = null
            }.apply {
                show()
            }
        }

        private fun inicializarProductos() {
            productos.addAll(listOf(
                Product(1, "Aceite de Oliva", "android.resource://${packageName}/${R.drawable.aceite_almacen}", true, 0, 19.99),
                Product(2, "Atún enlatado", "android.resource://${packageName}/${R.drawable.atun_almacen}", true, 0, 39.99),
                Product(3, "Botella de Agua", "android.resource://${packageName}/${R.drawable.botella_agua_almacen}", true, 0, 59.99),
                Product(4, "Chorizo", "android.resource://${packageName}/${R.drawable.chorizo_almacen}", true, 0, 14.99),
                Product(5, "Huevos", "android.resource://${packageName}/${R.drawable.huevos_almacen}", true, 0, 12.99),
                Product(6, "Pan", "android.resource://${packageName}/${R.drawable.pan_almacen}", true, 0, 9.99)
            ))
        }

        private val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                // Permiso concedido
            } else {
                Toast.makeText(this, "Se necesita permiso de cámara", Toast.LENGTH_SHORT).show()
            }
        }

        private fun checkCameraPermission() {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // Ya tiene permiso
                }
                ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.CAMERA
                ) -> {
                    // Explicar por qué se necesita el permiso
                    showPermissionExplanation()
                }
                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.CAMERA)
                }
            }
        }

        private fun showPermissionExplanation() {
            AlertDialog.Builder(this)
                .setTitle("Permiso requerido")
                .setMessage("Necesitamos acceso a la cámara para tomar fotos de los productos")
                .setPositiveButton("Aceptar") { _, _ ->
                    requestPermissionLauncher.launch(Manifest.permission.CAMERA)
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }

        override fun getCurrentDialog(): AddProductDialog {
            return currentAddProductDialog ?: throw IllegalStateException("Dialog not available")
        }

        override fun updateImagePreview(uri: Uri) {
            currentAddProductDialog?.updateImagePreview(uri)
        }
    }