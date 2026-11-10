package co.edu.compensar.tiendatech.ui.seller

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import co.edu.compensar.tiendatech.R
import co.edu.compensar.tiendatech.data.*
import co.edu.compensar.tiendatech.databinding.*
import co.edu.compensar.tiendatech.ui.*

/** CRUD de productos. El vendedor ve los suyos; el administrador ve todos. */
class ProductsFragment : VBFragment<FragmentListBinding>(FragmentListBinding::inflate), Titled {
    private val me get() = Repo.current!!
    override val title get() = if (Repo.current?.role == Role.ADMIN) "Gestión de productos" else "Mis productos"
    private lateinit var adapter: SimpleAdapter<Product>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        b.tilSearch.hint = "Buscar producto"
        adapter = SimpleAdapter(emptyList(), R.layout.item_product_manage) { v, p ->
            v.tv(R.id.tvName).text = p.name
            v.tv(R.id.tvInfo).text = "${p.category}  •  Stock: ${p.stock}"
            v.tv(R.id.tvPrice).text = Repo.money(p.price)
            v.findViewById<View>(R.id.btnEdit).setOnClickListener { showDialog(p) }
            v.findViewById<View>(R.id.btnDelete).setOnClickListener {
                MaterialAlertDialogBuilder(requireContext()).setTitle("Eliminar producto")
                    .setMessage("¿Eliminar ${p.name}?")
                    .setPositiveButton("Eliminar") { _, _ -> Repo.products.remove(p); refresh() }
                    .setNegativeButton("Cancelar", null).show()
            }
        }
        b.rvList.layoutManager = LinearLayoutManager(requireContext())
        b.rvList.adapter = adapter
        b.etSearch.doAfterTextChanged { refresh() }
        b.fabAdd.setOnClickListener { showDialog(null) }
        refresh()
    }

    private fun refresh() {
        val q = b.etSearch.text.toString().trim()
        val list = Repo.products.filter {
            (me.role == Role.ADMIN || it.sellerId == me.id) && (q.isEmpty() || it.name.contains(q, true))
        }
        adapter.update(list)
        b.tvEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun showDialog(p: Product?) {
        val d = DialogProductBinding.inflate(layoutInflater)
        d.actCategory.setItems(Repo.categories, p?.category ?: Repo.categories.first())
        if (p != null) {
            d.etName.setText(p.name); d.etPrice.setText(p.price.toString())
            d.etStock.setText(p.stock.toString()); d.etDesc.setText(p.description)
        }
        val dlg = MaterialAlertDialogBuilder(requireContext())
            .setTitle(if (p == null) "Nuevo producto" else "Editar producto")
            .setView(d.root)
            .setPositiveButton("Guardar", null)
            .setNegativeButton("Cancelar", null).create()
        dlg.show()
        dlg.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val name = d.etName.text.toString().trim()
            val price = d.etPrice.text.toString().toIntOrNull()
            val stock = d.etStock.text.toString().toIntOrNull()
            if (name.isEmpty() || price == null || price <= 0 || stock == null || stock < 0) {
                toast("Completa nombre, precio (>0) y cantidad"); return@setOnClickListener
            }
            val cat = d.actCategory.text.toString()
            val desc = d.etDesc.text.toString().trim()
            if (p == null) Repo.addProduct(name, cat, price, stock, desc, me.id)
            else { p.name = name; p.category = cat; p.price = price; p.stock = stock; p.description = desc }
            refresh(); dlg.dismiss()
        }
    }
}
