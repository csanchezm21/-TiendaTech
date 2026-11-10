package co.edu.compensar.tiendatech.ui.admin

import android.os.Bundle
import android.view.View
import co.edu.compensar.tiendatech.R
import co.edu.compensar.tiendatech.data.*
import co.edu.compensar.tiendatech.databinding.*
import co.edu.compensar.tiendatech.ui.*

class AdminDashboardFragment : VBFragment<FragmentAdminDashboardBinding>(FragmentAdminDashboardBinding::inflate), Titled {
    override val title = "Panel de administración"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        b.tvWelcome.text = "Hola, ${Repo.current?.name?.split(" ")?.first()}"
        b.tvUsers.text = Repo.users.size.toString()
        b.tvProducts.text = Repo.products.size.toString()
        b.tvOrders.text = Repo.orders.size.toString()
        b.tvSales.text = Repo.money(Repo.orders.sumOf { it.total })
        b.btnGoUsers.setOnClickListener { main().selectTab(R.id.nav_admin_users) }
        b.btnGoProducts.setOnClickListener { main().selectTab(R.id.nav_admin_products) }
        b.btnGoReport.setOnClickListener { main().selectTab(R.id.nav_admin_report) }
    }
}
