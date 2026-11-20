
package co.edu.compensar.tiendatech.ui.buyer

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.chip.Chip
import co.edu.compensar.tiendatech.R
import co.edu.compensar.tiendatech.data.*
import co.edu.compensar.tiendatech.databinding.*
import co.edu.compensar.tiendatech.ui.*
import kotlinx.coroutines.launch

class CatalogFragment :
    VBFragment<FragmentCatalogBinding>(FragmentCatalogBinding::inflate), Titled {

    override val title = "Catálogo"

    private lateinit var adapter: SimpleAdapter<Product>

    // Consulta los productos almacenados en Firebase.
    private val productRepository = FirebaseProductRepository()

    // Guarda los productos descargados para realizar las búsquedas.
    private var listaProductos = listOf<Product>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = productAdapter(
            emptyList(),
            onOpen = {
                (requireActivity() as MainActivity).navigate(
                    ProductDetailFragment().apply {
                        arguments = bundleOf("id" to it.id)
                    }
                )
            },
            onAdd = {
                Repo.addToCart(it)
                toast("${it.name} agregado al carrito")
            }
        )

        b.rvList.layoutManager = GridLayoutManager(requireContext(), 2)
        b.rvList.adapter = adapter

        // Crea los filtros de categorías.
        val initial = arguments?.getString("category") ?: ""

        (listOf("") + Repo.categories).forEach { categoria ->
            b.chipGroup.addView(
                Chip(requireContext()).apply {
                    text = if (categoria.isEmpty()) "Todos" else categoria
                    tag = categoria
                    isCheckable = true
                    isChecked = categoria == initial
                }
            )
        }

        b.chipGroup.setOnCheckedStateChangeListener { _, _ ->
            refresh()
        }

        // Actualiza la búsqueda cuando cambia el texto.
        b.etSearch.doAfterTextChanged {
            refresh()
        }

        // Consulta los productos al abrir el catálogo.
        cargarProductos()
    }

    // Descarga los productos desde Firestore.
    private fun cargarProductos() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                listaProductos = productRepository.obtenerProductos()


                listaProductos.forEach { producto ->
                    android.util.Log.d(
                        "TIENDATECH",
                        "Producto: ${producto.name} - Vendedor UID: ${producto.sellerUid}"
                    )
                }

                refresh()

            } catch (e: Exception) {
                toast("No se pudieron cargar los productos: ${e.localizedMessage}")
            }
        }
    }


    // Filtra por categoría, nombre y disponibilidad.
    private fun refresh() {
        val q = b.etSearch.text.toString().trim()

        val cat = b.chipGroup
            .findViewById<Chip>(b.chipGroup.checkedChipId)
            ?.tag as? String ?: ""

        val lista = listaProductos.filter {
            (cat.isEmpty() || it.category == cat) &&
                    (q.isEmpty() || it.name.contains(q, true)) &&
                    it.stock > 0
        }

        adapter.update(lista)

        b.tvEmpty.visibility =
            if (lista.isEmpty()) View.VISIBLE else View.GONE
    }
}
