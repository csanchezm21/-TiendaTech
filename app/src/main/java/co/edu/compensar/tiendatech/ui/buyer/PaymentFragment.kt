package co.edu.compensar.tiendatech.ui.buyer

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import co.edu.compensar.tiendatech.R
import co.edu.compensar.tiendatech.data.*
import co.edu.compensar.tiendatech.databinding.*
import co.edu.compensar.tiendatech.ui.*

/** Pasarela de pagos simulada (la integración real con un proveedor queda para la fase de backend). */
class PaymentFragment : VBFragment<FragmentPaymentBinding>(FragmentPaymentBinding::inflate), Titled {
    override val title = "Pasarela de pagos"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        b.tvTotal.text = Repo.money(Repo.cartTotal())
        b.tvSummary.text = "${Repo.cart.sumOf { it.qty }} artículos en tu pedido"

        b.btnConfirm.setOnClickListener {
            if (Repo.cart.isEmpty()) { toast("Tu carrito está vacío"); return@setOnClickListener }
            if (b.etAddress.text.toString().trim().length < 6) {
                b.tilAddress.error = "Ingresa una dirección válida"; return@setOnClickListener
            }
            b.tilAddress.error = null
            b.progress.visibility = View.VISIBLE
            b.btnConfirm.isEnabled = false
            Handler(Looper.getMainLooper()).postDelayed({
                if (getView() == null || !isAdded) return@postDelayed
                val order = Repo.checkout(Repo.current!!)
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("¡Pago aprobado!")
                    .setMessage("Tu pedido #${order.id} por ${Repo.money(order.total)} fue registrado. Conserva este mensaje como confirmación de compra.")
                    .setCancelable(false)
                    .setPositiveButton("Ver mis compras") { _, _ ->
                        main().selectTab(R.id.nav_buyer_profile)
                        main().navigate(co.edu.compensar.tiendatech.ui.shared.OrdersFragment())
                    }.show()
            }, 1500)
        }
    }
}
