package co.edu.compensar.tiendatech.ui.shared

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import co.edu.compensar.tiendatech.R
import co.edu.compensar.tiendatech.data.*
import co.edu.compensar.tiendatech.databinding.*
import co.edu.compensar.tiendatech.ui.*

fun orderAdapter(list: List<Order>, canUpdate: Boolean, onChange: () -> Unit = {}) =
    SimpleAdapter(list, R.layout.item_order) { v, o ->
        v.tv(R.id.tvOrderId).text = "Pedido #${o.id}"
        v.tv(R.id.tvStatus).text = o.status
        v.tv(R.id.tvBuyer).text = "Cliente: ${o.buyerName}"
        v.tv(R.id.tvItems).text = o.items
        v.tv(R.id.tvTotal).text = Repo.money(o.total)
        val btn = v.findViewById<View>(R.id.btnStatus)
        btn.visibility = if (canUpdate && o.status != "Entregado") View.VISIBLE else View.GONE
        btn.setOnClickListener {
            val next = Repo.statuses.indexOf(o.status) + 1
            if (next < Repo.statuses.size) { o.status = Repo.statuses[next]; onChange() }
        }
    }

/** Vendedor: pedidos recibidos (puede cambiar el estado). Comprador: sus compras. */
class OrdersFragment : VBFragment<FragmentListBinding>(FragmentListBinding::inflate), Titled {
    private val me get() = Repo.current!!
    override val title get() = if (Repo.current?.role == Role.SELLER) "Pedidos recibidos" else "Mis compras"
    private lateinit var adapter: SimpleAdapter<Order>

    private fun data() = if (me.role == Role.SELLER) Repo.orders.filter { it.sellerId == me.id }
                         else Repo.orders.filter { it.buyerId == me.id }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        b.tilSearch.visibility = View.GONE
        b.fabAdd.visibility = View.GONE
        adapter = orderAdapter(data(), me.role == Role.SELLER) { adapter.update(data()) }
        b.rvList.layoutManager = LinearLayoutManager(requireContext())
        b.rvList.adapter = adapter
        b.tvEmpty.text = "Aún no hay pedidos"
        b.tvEmpty.visibility = if (data().isEmpty()) View.VISIBLE else View.GONE
    }
}
