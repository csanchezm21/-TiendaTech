
package co.edu.compensar.tiendatech.data

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

// Administra los productos almacenados en Firestore.
class FirebaseProductRepository {

    // Conexión con Firebase.
    private val db = FirebaseFirestore.getInstance()

    // Colección de productos.
    private val productos = db.collection("productos")

    // Consulta los productos guardados.
    suspend fun obtenerProductos(): List<Product> {

        val resultado = productos.get().await()

        return resultado.documents.mapNotNull { documento ->

            val perfil = documento.toObject(ProductProfile::class.java)
                ?: return@mapNotNull null

            Product(
                id = documento.id.hashCode(),
                name = perfil.name,
                category = perfil.category,
                price = perfil.price,
                stock = perfil.stock,
                description = perfil.description,
                sellerId = perfil.sellerUid.hashCode(),
                firebaseId = documento.id,
                sellerUid = perfil.sellerUid
            )
        }
    }

    // Guarda un producto nuevo en Firestore.
    suspend fun guardarProducto(
        nombre: String,
        categoria: String,
        precio: Int,
        stock: Int,
        descripcion: String,
        vendedorUid: String
    ) {

        val perfil = ProductProfile(
            name = nombre,
            category = categoria,
            price = precio,
            stock = stock,
            description = descripcion,
            sellerUid = vendedorUid
        )

        productos.add(perfil).await()
    }

    // Actualiza la información de un producto.
    suspend fun actualizarProducto(
        id: String,
        nombre: String,
        categoria: String,
        precio: Int,
        stock: Int,
        descripcion: String
    ) {

        productos.document(id).update(
            mapOf(
                "name" to nombre,
                "category" to categoria,
                "price" to precio,
                "stock" to stock,
                "description" to descripcion
            )
        ).await()
    }

    // Elimina un producto de Firestore.
    suspend fun eliminarProducto(id: String) {
        productos.document(id).delete().await()
    }
}
