
package co.edu.compensar.tiendatech.ui.buyer

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import co.edu.compensar.tiendatech.R
import co.edu.compensar.tiendatech.data.*
import co.edu.compensar.tiendatech.databinding.*
import co.edu.compensar.tiendatech.ui.*
import kotlinx.coroutines.launch

// Proceso de compra con registro del pedido en Firebase.
class PaymentFragment :
    VBFragment<FragmentPaymentBinding>(FragmentPaymentBinding::inflate), Titled {

    override val title = "Pasarela de pagos"

    private val orderRepository = FirebaseOrderRepository()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        b.tvTotal.text = Repo.money(Repo.cartTotal())
        b.tvSummary.text = "${Repo.cart.sumOf { it.qty }} artículos en tu pedido"

        b.btnConfirm.setOnClickListener {
            if (Repo.cart.isEmpty()) {
                toast("Tu carrito está vacío")
                return@setOnClickListener
            }

            val address = b.etAddress.text.toString().trim()

            if (address.length < 6) {
                b.tilAddress.error = "Ingresa una dirección válida"
                return@setOnClickListener
            }

            b.tilAddress.error = null

            val buyer = Repo.current
            if (buyer == null) {
                toast("No se encontró la sesión del comprador")
                return@setOnClickListener
            }

            val uid = buyer.firebaseUid
                ?: FirebaseAuth.getInstance().currentUser?.uid

            if (uid.isNullOrBlank()) {
                toast("No se pudo identificar al comprador")
                return@setOnClickListener
            }

            b.progress.visibility = View.VISIBLE
            b.btnConfirm.isEnabled = false

            // Guardamos el pedido antes de vaciar el carrito.
            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    // Creamos el pedido con los identificadores de Firebase.
                    val pedidoPendiente = Order(
                        id = 0,
                        buyerId = buyer.id,
                        buyerName = buyer.name,
                        items = Repo.cart.joinToString(", ") {
                            "${it.product.name} x${it.qty}"
                        },
                        total = Repo.cartTotal(),
                        status = "Pendiente",
                        sellerId = Repo.cart.first().product.sellerId,
                        buyerUid = uid,
                        address = address,
                        sellerUid = Repo.cart.first().product.sellerUid
                    )

                    // Firebase devuelve el identificador del pedido.
                    val firebaseId = orderRepository.guardarPedido(pedidoPendiente)
                    android.util.Log.d(
                        "PEDIDOS",
                        "Pedido guardado: $firebaseId | Comprador: $uid | Vendedor: ${pedidoPendiente.sellerUid}"
                    )
                    // Solo después de guardar correctamente vaciamos el carrito.
                    val order = Repo.checkout(buyer)
                    order.firebaseId = firebaseId
                    order.buyerUid = uid
                    order.address = address

                    b.progress.visibility = View.GONE

                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle("¡Pedido registrado!")
                        .setMessage(
                            "Tu pedido #$firebaseId por ${Repo.money(order.total)} fue registrado correctamente."
                        )
                        .setCancelable(false)
                        .setPositiveButton("Ver mis compras") { _, _ ->
                            (requireActivity() as MainActivity).navigate(
                                co.edu.compensar.tiendatech.ui.shared.OrdersFragment()
                            )
                        }
                        .show()

                } catch (e: Exception) {
                    b.progress.visibility = View.GONE
                    b.btnConfirm.isEnabled = true
                    toast("No se pudo registrar el pedido: ${e.localizedMessage}")
                }
            }
        }
    }
}
