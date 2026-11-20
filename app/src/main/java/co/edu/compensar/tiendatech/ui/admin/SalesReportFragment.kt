
package co.edu.compensar.tiendatech.ui.admin

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import co.edu.compensar.tiendatech.data.*
import co.edu.compensar.tiendatech.databinding.*
import co.edu.compensar.tiendatech.ui.*
import co.edu.compensar.tiendatech.ui.shared.orderAdapter
import kotlinx.coroutines.launch

class SalesReportFragment :
    VBFragment<FragmentReportBinding>(FragmentReportBinding::inflate), Titled {

    override val title = "Reporte de ventas"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configuramos la lista de pedidos.
        b.rvList.layoutManager = LinearLayoutManager(requireContext())

        // Consultamos los pedidos registrados en Firebase.
        cargarReporte()
    }

    private fun cargarReporte() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                // Obtenemos el administrador que inició sesión.
                val usuario = Repo.current
                    ?: throw Exception("No se encontró el usuario actual.")

                // Consultamos los pedidos almacenados en Firebase.
                val repositorio = FirebaseOrderRepository()
                val pedidos = repositorio.obtenerPedidos(
                    usuario.firebaseUid.orEmpty(),
                    usuario.role
                )

                // Calculamos las ventas y el promedio por pedido.
                val total = pedidos.sumOf { it.total }
                val promedio = if (pedidos.isEmpty()) {
                    0
                } else {
                    total / pedidos.size
                }

                // Actualizamos los indicadores del reporte.
                b.tvTotal.text = Repo.money(total)
                b.tvSummary.text =
                    "${pedidos.size} compras • Ticket promedio ${Repo.money(promedio)}"

                // Mostramos los pedidos obtenidos desde Firebase.
                b.rvList.adapter = orderAdapter(pedidos, false)

            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    "Error al cargar el reporte: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}
