package co.edu.compensar.tiendatech.ui.buyer

import android.os.Bundle
import android.view.View
import co.edu.compensar.tiendatech.R
import co.edu.compensar.tiendatech.data.*
import co.edu.compensar.tiendatech.databinding.*
import co.edu.compensar.tiendatech.ui.*

class ProductDetailFragment : VBFragment<FragmentProductDetailBinding>(FragmentProductDetailBinding::inflate), Titled {
    override val title = "Detalle del producto"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val id = requireArguments().getInt("id")
        val p = Repo.products.first { it.id == id }
        var qty = 1
        b.tvName.text = p.name
        b.tvCategory.text = p.category
        b.tvPrice.text = Repo.money(p.price)
        b.tvStock.text = "Disponibles: ${p.stock}"
        b.tvDesc.text = p.description
        b.btnMinus.setOnClickListener { if (qty > 1) { qty--; b.tvQty.text = qty.toString() } }
        b.btnPlus.setOnClickListener {
            if (qty < p.stock) { qty++; b.tvQty.text = qty.toString() } else toast("No hay más unidades disponibles")
        }
        b.btnAdd.setOnClickListener {
            Repo.addToCart(p, qty)
            toast("Agregado al carrito")
            parentFragmentManager.popBackStack()
        }
    }
}
