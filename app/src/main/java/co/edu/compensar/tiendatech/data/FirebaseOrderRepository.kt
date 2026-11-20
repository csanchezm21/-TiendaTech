
package co.edu.compensar.tiendatech.data

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

// Repositorio encargado de gestionar los pedidos en Firebase.
class FirebaseOrderRepository {

    private val db = FirebaseFirestore.getInstance()
    private val pedidos = db.collection("pedidos")

    // Guarda un pedido y devuelve su identificador.
    suspend fun guardarPedido(pedido: Order): String {
        val documento = pedidos.document()

        val datos = mapOf(
            "buyerId" to pedido.buyerId,
            "buyerName" to pedido.buyerName,
            "buyerUid" to pedido.buyerUid,
            "items" to pedido.items,
            "total" to pedido.total,
            "status" to pedido.status,
            "sellerId" to pedido.sellerId,
            "sellerUid" to pedido.sellerUid,
            "address" to pedido.address
        )

        documento.set(datos).await()
        return documento.id
    }

    // Consulta pedidos según el rol del usuario.
    suspend fun obtenerPedidos(uid: String, rol: Role): List<Order> {
        val consulta = when (rol) {
            Role.ADMIN -> pedidos
            Role.SELLER -> pedidos.whereEqualTo("sellerUid", uid)
            Role.BUYER -> pedidos.whereEqualTo("buyerUid", uid)
        }

        val resultado = consulta.get().await()

        return resultado.documents.mapNotNull { documento ->
            Order(
                id = documento.id.hashCode(),
                buyerId = (documento.getLong("buyerId") ?: 0).toInt(),
                buyerName = documento.getString("buyerName") ?: "",
                items = documento.getString("items") ?: "",
                total = (documento.getLong("total") ?: 0).toInt(),
                status = documento.getString("status") ?: "Pendiente",
                sellerId = (documento.getLong("sellerId") ?: 0).toInt(),
                firebaseId = documento.id,
                buyerUid = documento.getString("buyerUid") ?: "",
                address = documento.getString("address") ?: "",
                sellerUid = documento.getString("sellerUid") ?: ""
            )
        }
    }

    // Actualiza el estado de un pedido.
    suspend fun actualizarEstado(firebaseId: String, estado: String) {
        pedidos.document(firebaseId)
            .update("status", estado)
            .await()
    }
}

