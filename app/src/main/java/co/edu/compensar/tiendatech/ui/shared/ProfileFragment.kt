package co.edu.compensar.tiendatech.ui.shared

import android.os.Bundle
import android.view.View
import co.edu.compensar.tiendatech.R
import co.edu.compensar.tiendatech.data.*
import co.edu.compensar.tiendatech.databinding.*
import co.edu.compensar.tiendatech.ui.*

/** Perfil común para los tres roles. */
class ProfileFragment : VBFragment<FragmentProfileBinding>(FragmentProfileBinding::inflate), Titled {
    override val title = "Mi perfil"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val u = Repo.current!!
        fun paint() {
            b.tvAvatar.text = u.name.take(1).uppercase()
            b.tvName.text = u.name
            b.tvEmail.text = u.email
            b.tvRole.text = u.role.label
        }
        paint()
        b.etName.setText(u.name)
        b.btnMyOrders.visibility = if (u.role == Role.BUYER) View.VISIBLE else View.GONE
        b.btnMyOrders.setOnClickListener { main().navigate(OrdersFragment()) }
        b.btnSave.setOnClickListener {
            val n = b.etName.text.toString().trim()
            if (n.length < 3) { b.tilName.error = "Nombre muy corto"; return@setOnClickListener }
            b.tilName.error = null
            u.name = n; paint(); toast("Perfil actualizado")
        }
        b.btnLogout.setOnClickListener { main().logout() }
    }
}
