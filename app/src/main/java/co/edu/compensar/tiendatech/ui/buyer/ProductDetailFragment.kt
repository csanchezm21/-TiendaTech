
package co.edu.compensar.tiendatech.ui.buyer

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import co.edu.compensar.tiendatech.data.*
import co.edu.compensar.tiendatech.databinding.*
import co.edu.compensar.tiendatech.ui.*
import kotlinx.coroutines.launch

class ProductDetailFragment :
    VBFragment<FragmentProductDetailBinding>(FragmentProductDetailBinding::inflate),
    Titled {

    override val title = "Detalle del producto"

    private val productRepository = FirebaseProductRepository()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Obtiene el identificador enviado desde el catálogo.
        val id = requireArguments().getInt("id")

        cargarProducto(id)
    }

    // Consulta el producto seleccionado en Firestore.
    private fun cargarProducto(id: Int) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val productos = productRepository.obtenerProductos()
                val producto = productos.firstOrNull { it.id == id }

                if (producto == null) {
                    toast("No se encontró el producto")
                    parentFragmentManager.popBackStack()
                    return@launch
                }

                mostrarProducto(producto)

            } catch (e: Exception) {
                toast("No se pudo cargar el producto: ${e.localizedMessage}")
            }
        }
    }

    // Muestra la información y controla las cantidades.
    private fun mostrarProducto(p: Product) {
        var qty = 1

        b.tvName.text = p.name
        b.tvCategory.text = p.category
        b.tvPrice.text = Repo.money(p.price)
        b.tvStock.text = "Disponibles: ${p.stock}"
        b.tvDesc.text = p.description
        b.tvQty.text = qty.toString()

        b.btnMinus.setOnClickListener {
            if (qty > 1) {
                qty--
                b.tvQty.text = qty.toString()
            }
        }

        b.btnPlus.setOnClickListener {
            if (qty < p.stock) {
                qty++
                b.tvQty.text = qty.toString()
            } else {
                toast("No hay más unidades disponibles")
            }
        }

        b.btnAdd.setOnClickListener {
            if (p.stock <= 0) {
                toast("Este producto no tiene existencias")
                return@setOnClickListener
            }

            Repo.addToCart(p, qty)
            toast("Agregado al carrito")
            parentFragmentManager.popBackStack()
        }
    }
}

