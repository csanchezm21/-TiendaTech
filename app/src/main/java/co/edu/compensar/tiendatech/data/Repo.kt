package co.edu.compensar.tiendatech.data

import java.text.NumberFormat
import java.util.Locale

/** Repositorio en memoria con datos de ejemplo (la conexión a backend queda para una fase posterior). */
object Repo {
    val categories = listOf("Audio", "Cómputo", "Carga", "Accesorios")
    val statuses = listOf("Pendiente", "Preparando", "Enviado", "Entregado")

    val users = mutableListOf(
        User(1, "Ana Administradora", "admin@tienda.com", "Admin123", Role.ADMIN),
        User(2, "Victor Vendedor", "vendedor@tienda.com", "Vend123", Role.SELLER),
        User(3, "Carlos Comprador", "comprador@tienda.com", "Comp123", Role.BUYER)
    )
    val products = mutableListOf(
        Product(1, "Audífonos Bluetooth", "Audio", 89900, 25, "Audífonos inalámbricos con cancelación de ruido y 20 horas de batería.", 2),
        Product(2, "Mouse inalámbrico", "Cómputo", 45000, 40, "Mouse ergonómico 2.4 GHz con receptor USB y 1600 DPI.", 2),
        Product(3, "Teclado mecánico", "Cómputo", 129000, 15, "Teclado mecánico retroiluminado con switches rojos.", 2),
        Product(4, "Cargador rápido 35W", "Carga", 52000, 60, "Cargador USB-C de carga rápida compatible con Android y iPhone.", 2),
        Product(5, "Cable USB-C 1 m", "Carga", 18000, 100, "Cable trenzado reforzado, carga y transferencia de datos.", 2),
        Product(6, "Parlante portátil", "Audio", 119000, 12, "Parlante resistente al agua con 12 horas de reproducción.", 2),
        Product(7, "Soporte para celular", "Accesorios", 24000, 50, "Soporte ajustable para escritorio, compatible con todos los tamaños.", 2),
        Product(8, "Power bank 10000 mAh", "Carga", 95000, 30, "Batería externa con dos puertos y pantalla de nivel de carga.", 2)
    )
    val cart = mutableListOf<CartItem>()
    val orders = mutableListOf(
        Order(1, 3, "Carlos Comprador", "Audífonos Bluetooth x1", 89900, "Entregado", 2),
        Order(2, 3, "Carlos Comprador", "Mouse inalámbrico x2, Cable USB-C 1 m x1", 108000, "Enviado", 2),
        Order(3, 3, "Carlos Comprador", "Teclado mecánico x1", 129000, "Pendiente", 2)
    )
    var current: User? = null

    private var nextUser = 4
    private var nextProduct = 9
    private var nextOrder = 4

    fun login(email: String, pass: String) =
        users.firstOrNull { it.email.equals(email, true) && it.password == pass && it.active }

    fun addUser(name: String, email: String, pass: String, role: Role) {
        users.add(User(nextUser++, name, email, pass, role))
    }

    fun emailExists(email: String, exceptId: Int = -1) =
        users.any { it.email.equals(email, true) && it.id != exceptId }

    fun addProduct(name: String, cat: String, price: Int, stock: Int, desc: String, sellerId: Int) {
        products.add(Product(nextProduct++, name, cat, price, stock, desc, sellerId))
    }

    fun addToCart(p: Product, qty: Int = 1) {
        val item = cart.firstOrNull { it.product.id == p.id }
        if (item != null) item.qty += qty else cart.add(CartItem(p, qty))
    }

    fun cartTotal() = cart.sumOf { it.product.price * it.qty }

    fun checkout(buyer: User): Order {
        val order = Order(
            nextOrder++, buyer.id, buyer.name,
            cart.joinToString(", ") { "${it.product.name} x${it.qty}" },
            cartTotal(), "Pendiente", cart.first().product.sellerId
        )
        cart.forEach { it.product.stock = (it.product.stock - it.qty).coerceAtLeast(0) }
        orders.add(0, order)
        cart.clear()
        return order
    }

    fun money(v: Int): String = "\$" + NumberFormat.getInstance(Locale("es", "CO")).format(v)
}
