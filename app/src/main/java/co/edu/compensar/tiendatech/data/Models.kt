package co.edu.compensar.tiendatech.data

enum class Role(val label: String) {
    ADMIN("Administrador"), SELLER("Vendedor"), BUYER("Comprador")
}


data class User(
    val id: Int,
    var name: String,
    var email: String,
    var password: String,
    var role: Role,
    var active: Boolean = true,
    var firebaseUid: String? = null
)

data class UserProfile(
    val name: String = "",
    val email: String = "",
    val role: String = "BUYER",
    val active: Boolean = true
)



data class Product(
    val id: Int,
    var name: String,
    var category: String,
    var price: Int,
    var stock: Int,
    var description: String,
    val sellerId: Int,
    var firebaseId: String? = null,
    var sellerUid: String = ""
)



data class CartItem(val product: Product, var qty: Int)

data class Order(
    val id: Int,
    val buyerId: Int,
    val buyerName: String,
    val items: String,
    val total: Int,
    var status: String,
    val sellerId: Int,
    var firebaseId: String? = null,
    var buyerUid: String = "",
    var address: String = "",
    var sellerUid: String = ""
)


data class ProductProfile(
    val name: String = "",
    val category: String = "",
    val price: Int = 0,
    val stock: Int = 0,
    val description: String = "",
    val sellerUid: String = ""
)

