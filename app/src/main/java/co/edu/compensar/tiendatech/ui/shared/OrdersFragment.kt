
package co.edu.compensar.tiendatech.ui.shared

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import co.edu.compensar.tiendatech.R
import co.edu.compensar.tiendatech.data.*
import co.edu.compensar.tiendatech.databinding.*
import co.edu.compensar.tiendatech.ui.*
import kotlinx.coroutines.launch

// Construye la lista visual de pedidos.
fun orderAdapter(
    list: List<Order>,
    canUpdate: Boolean,
    onChange: (Order, String) -> Unit = { _, _ -> }
) = SimpleAdapter(list, R.layout.item_order) { v, o ->

    v.tv(R.id.tvOrderId).text = "Pedido #${o.firebaseId?.take(8) ?: o.id}"
    v.tv(R.id.tvStatus).text = o.status
    v.tv(R.id.tvBuyer).text = "Cliente: ${o.buyerName}"
    v.tv(R.id.tvItems).text = o.items
    v.tv(R.id.tvTotal).text = Repo.money(o.total)

    val btn = v.findViewById<View>(R.id.btnStatus)

    btn.visibility = if (canUpdate && o.status != "Entregado") {
        View.VISIBLE
    } else {
        View.GONE
    }

    // Calcula el siguiente estado sin modificarlo localmente.
    btn.setOnClickListener {
        val next = Repo.statuses.indexOf(o.status) + 1

        if (next in Repo.statuses.indices) {
            onChange(o, Repo.statuses[next])
        }
    }
}

// Consulta y administra los pedidos almacenados en Firebase.
class OrdersFragment :
    VBFragment<FragmentListBinding>(FragmentListBinding::inflate), Titled {

    private val me get() = Repo.current!!
    private val orderRepository = FirebaseOrderRepository()

    override val title: String
        get() = when (me.role) {
            Role.SELLER -> "Pedidos recibidos"
            Role.ADMIN -> "Todos los pedidos"
            else -> "Mis compras"
        }

    private lateinit var adapter: SimpleAdapter<Order>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        b.tilSearch.visibility = View.GONE
        b.fabAdd.visibility = View.GONE
        b.tvEmpty.text = "Aún no hay pedidos"

        // Configura la lista de pedidos.
        adapter = orderAdapter(
            emptyList(),
            me.role == Role.SELLER || me.role == Role.ADMIN
        ) { pedido, nuevoEstado ->
            cambiarEstado(pedido, nuevoEstado)
        }

        b.rvList.layoutManager = LinearLayoutManager(requireContext())
        b.rvList.adapter = adapter

        cargarPedidos()
    }

    // Obtiene los pedidos correspondientes al usuario conectado.
    private fun cargarPedidos() {
        val uid = me.firebaseUid

        if (uid.isNullOrBlank()) {
            toast("No se encontró el usuario conectado")
            return
        }

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val lista = orderRepository.obtenerPedidos(uid, me.role)

                adapter.update(lista)
                b.tvEmpty.visibility = if (lista.isEmpty()) {
                    View.VISIBLE
                } else {
                    View.GONE
                }

            } catch (e: Exception) {
                toast("No se pudieron cargar los pedidos: ${e.localizedMessage}")
            }
        }
    }

    // Guarda en Firebase el nuevo estado del pedido.
    private fun cambiarEstado(pedido: Order, nuevoEstado: String) {
        val id = pedido.firebaseId ?: return

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                orderRepository.actualizarEstado(id, nuevoEstado)
                toast("Estado actualizado correctamente")
                cargarPedidos()

            } catch (e: Exception) {
                toast("No se pudo actualizar el pedido: ${e.localizedMessage}")
            }
        }
    }
}
