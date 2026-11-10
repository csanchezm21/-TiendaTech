package co.edu.compensar.tiendatech.data

enum class Role(val label: String) {
    ADMIN("Administrador"), SELLER("Vendedor"), BUYER("Comprador")
}

data class User(
    val id: Int, var name: String, var email: String,
    var password: String, var role: Role, var active: Boolean = true
)

data class Product(
    val id: Int, var name: String, var category: String, var price: Int,
    var stock: Int, var description: String, val sellerId: Int
)

data class CartItem(val product: Product, var qty: Int)

data class Order(
    val id: Int, val buyerId: Int, val buyerName: String, val items: String,
    val total: Int, var status: String, val sellerId: Int
)
