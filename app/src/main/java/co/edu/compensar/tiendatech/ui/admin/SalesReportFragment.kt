package co.edu.compensar.tiendatech.ui.admin

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import co.edu.compensar.tiendatech.R
import co.edu.compensar.tiendatech.data.*
import co.edu.compensar.tiendatech.databinding.*
import co.edu.compensar.tiendatech.ui.*
import co.edu.compensar.tiendatech.ui.shared.orderAdapter

class SalesReportFragment : VBFragment<FragmentReportBinding>(FragmentReportBinding::inflate), Titled {
    override val title = "Reporte de ventas"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val total = Repo.orders.sumOf { it.total }
        val avg = if (Repo.orders.isEmpty()) 0 else total / Repo.orders.size
        b.tvTotal.text = Repo.money(total)
        b.tvSummary.text = "${Repo.orders.size} compras  •  Ticket promedio ${Repo.money(avg)}"
        b.rvList.layoutManager = LinearLayoutManager(requireContext())
        b.rvList.adapter = orderAdapter(Repo.orders, false)
    }
}
