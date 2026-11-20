package co.edu.compensar.tiendatech.ui.seller

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import co.edu.compensar.tiendatech.R
import co.edu.compensar.tiendatech.data.*
import co.edu.compensar.tiendatech.databinding.*
import co.edu.compensar.tiendatech.ui.*
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

/** CRUD de productos. El vendedor ve los suyos; el administrador ve todos. */
class ProductsFragment : VBFragment<FragmentListBinding>(FragmentListBinding::inflate), Titled {
    private val me get() = Repo.current!!
    override val title get() = if (Repo.current?.role == Role.ADMIN) "Gestión de productos" else "Mis productos"
    private lateinit var adapter: SimpleAdapter<Product>

    // Permite consultar y administrar productos en Firestore.
    private val productRepository = FirebaseProductRepository()


    private val currentUid: String?
        get() = me.firebaseUid

    private var listaProductos = listOf<Product>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        b.tilSearch.hint = "Buscar producto"
        adapter = SimpleAdapter(emptyList(), R.layout.item_product_manage) { v, p ->
            v.tv(R.id.tvName).text = p.name
            v.tv(R.id.tvInfo).text = "${p.category}  •  Stock: ${p.stock}"
            v.tv(R.id.tvPrice).text = Repo.money(p.price)
            v.findViewById<View>(R.id.btnEdit).setOnClickListener { showDialog(p) }
            v.findViewById<View>(R.id.btnDelete).setOnClickListener {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Eliminar producto")
                    .setMessage("¿Eliminar ${p.name}?")
                    .setPositiveButton("Eliminar") { _, _ ->

                        val id = p.firebaseId

                        if (id.isNullOrBlank()) {
                            toast("No se encontró el identificador del producto")
                            return@setPositiveButton
                        }

                        viewLifecycleOwner.lifecycleScope.launch {
                            try {
                                productRepository.eliminarProducto(id)
                                toast("Producto eliminado correctamente")
                                cargarProductos()

                            } catch (e: Exception) {
                                toast("No se pudo eliminar el producto: ${e.localizedMessage}")
                            }
                        }
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }
        }
        b.rvList.layoutManager = LinearLayoutManager(requireContext())
        b.rvList.adapter = adapter
        b.etSearch.doAfterTextChanged { refresh() }
        b.fabAdd.setOnClickListener { showDialog(null) }
        cargarProductos()
    }
    // Consulta los productos almacenados en Firestore.
    // Consulta los productos almacenados en Firestore.
    private fun cargarProductos() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                listaProductos = productRepository.obtenerProductos()
                refresh()

            } catch (e: Exception) {
                toast("No se pudieron consultar los productos: ${e.localizedMessage}")
            }
        }
    }
    // Filtra los productos por usuario y texto de búsqueda.
    private fun refresh() {
        val q = b.etSearch.text.toString().trim()

        val lista = listaProductos.filter {
            (me.role == Role.ADMIN || it.sellerUid == currentUid) &&
                    (q.isEmpty() || it.name.contains(q, true))
        }

        adapter.update(lista)

        b.tvEmpty.visibility =
            if (lista.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun showDialog(p: Product?) {
        val d = DialogProductBinding.inflate(layoutInflater)
        d.actCategory.setItems(Repo.categories, p?.category ?: Repo.categories.first())
        if (p != null) {
            d.etName.setText(p.name); d.etPrice.setText(p.price.toString())
            d.etStock.setText(p.stock.toString()); d.etDesc.setText(p.description)
        }
        val dlg = MaterialAlertDialogBuilder(requireContext())
            .setTitle(if (p == null) "Nuevo producto" else "Editar producto")
            .setView(d.root)
            .setPositiveButton("Guardar", null)
            .setNegativeButton("Cancelar", null).create()
        dlg.show()
        dlg.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val name = d.etName.text.toString().trim()
            val price = d.etPrice.text.toString().toIntOrNull()
            val stock = d.etStock.text.toString().toIntOrNull()
            if (name.isEmpty() || price == null || price <= 0 || stock == null || stock < 0) {
                toast("Completa nombre, precio (>0) y cantidad"); return@setOnClickListener
            }
            val cat = d.actCategory.text.toString()
            val desc = d.etDesc.text.toString().trim()

// Verifica que exista el UID del usuario conectado.
            val uid = currentUid

            if (uid.isNullOrBlank()) {
                toast("No se encontró la sesión del usuario")
                return@setOnClickListener
            }

// Guarda o actualiza el producto en Firestore.
            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    if (p == null) {
                        productRepository.guardarProducto(
                            name, cat, price, stock, desc, uid
                        )
                        toast("Producto creado correctamente")
                    } else {
                        val id = p.firebaseId

                        if (id.isNullOrBlank()) {
                            toast("No se encontró el identificador del producto")
                            return@launch
                        }

                        productRepository.actualizarProducto(
                            id, name, cat, price, stock, desc
                        )
                        toast("Producto actualizado correctamente")
                    }

                    dlg.dismiss()
                    cargarProductos()

                } catch (e: Exception) {
                    toast("No se pudo guardar el producto: ${e.localizedMessage}")
                }
            }
        }
    }
}
