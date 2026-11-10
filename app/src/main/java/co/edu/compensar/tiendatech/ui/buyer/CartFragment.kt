package co.edu.compensar.tiendatech.ui.buyer

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import co.edu.compensar.tiendatech.R
import co.edu.compensar.tiendatech.data.*
import co.edu.compensar.tiendatech.databinding.*
import co.edu.compensar.tiendatech.ui.*

class CartFragment : VBFragment<FragmentCartBinding>(FragmentCartBinding::inflate), Titled {
    override val title = "Mi carrito"
    private lateinit var adapter: SimpleAdapter<CartItem>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = SimpleAdapter(emptyList(), R.layout.item_cart) { v, item ->
            v.tv(R.id.tvName).text = item.product.name
            v.tv(R.id.tvPrice).text = Repo.money(item.product.price * item.qty)
            v.tv(R.id.tvQty).text = item.qty.toString()
            v.findViewById<View>(R.id.btnMinus).setOnClickListener { if (item.qty > 1) { item.qty--; refresh() } }
            v.findViewById<View>(R.id.btnPlus).setOnClickListener {
                if (item.qty < item.product.stock) { item.qty++; refresh() } else toast("Sin más stock")
            }
            v.findViewById<View>(R.id.btnRemove).setOnClickListener { Repo.cart.remove(item); refresh() }
        }
        b.rvCart.layoutManager = LinearLayoutManager(requireContext())
        b.rvCart.adapter = adapter
        b.btnPay.setOnClickListener { main().navigate(PaymentFragment()) }
        refresh()
    }

    private fun refresh() {
        adapter.update(Repo.cart.toList())
        val empty = Repo.cart.isEmpty()
        b.tvEmpty.visibility = if (empty) View.VISIBLE else View.GONE
        b.panelTotal.visibility = if (empty) View.GONE else View.VISIBLE
        b.tvTotal.text = Repo.money(Repo.cartTotal())
    }
}
