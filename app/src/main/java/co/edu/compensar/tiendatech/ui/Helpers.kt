package co.edu.compensar.tiendatech.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import co.edu.compensar.tiendatech.R
import co.edu.compensar.tiendatech.data.Product

/** Fragmentos que declaran el título que se muestra en la Toolbar. */
interface Titled { val title: String }

/** Fragmento base con ViewBinding: evita repetir el código de inflado y limpieza. */
abstract class VBFragment<T : ViewBinding>(
    private val inflate: (LayoutInflater, ViewGroup?, Boolean) -> T
) : Fragment() {
    private var _b: T? = null
    protected val b: T get() = _b!!
    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: android.os.Bundle?): View {
        _b = inflate(i, c, false); return b.root
    }
    override fun onDestroyView() { super.onDestroyView(); _b = null }
}

/** Adaptador genérico: recibe el layout del ítem y una función que enlaza los datos. */
class SimpleAdapter<T>(
    private var items: List<T>,
    private val layout: Int,
    private val bind: (View, T) -> Unit
) : RecyclerView.Adapter<SimpleAdapter.VH>() {
    class VH(v: View) : RecyclerView.ViewHolder(v)
    override fun onCreateViewHolder(p: ViewGroup, t: Int) =
        VH(LayoutInflater.from(p.context).inflate(layout, p, false))
    override fun getItemCount() = items.size
    override fun onBindViewHolder(h: VH, i: Int) = bind(h.itemView, items[i])
    fun update(n: List<T>) { items = n; notifyDataSetChanged() }
}

fun View.tv(id: Int) = findViewById<TextView>(id)
fun Fragment.toast(m: String) = Toast.makeText(requireContext(), m, Toast.LENGTH_SHORT).show()
fun Fragment.main() = requireActivity() as MainActivity
fun AutoCompleteTextView.setItems(items: List<String>, selected: String?) {
    setAdapter(ArrayAdapter(context, android.R.layout.simple_list_item_1, items))
    if (selected != null) setText(selected, false)
}

/** Adaptador de tarjetas de producto usado en Inicio y Catálogo. */
fun productAdapter(list: List<Product>, onOpen: (Product) -> Unit, onAdd: (Product) -> Unit) =
    SimpleAdapter(list, R.layout.item_product_card) { v, p ->
        v.tv(R.id.tvName).text = p.name
        v.tv(R.id.tvCategory).text = p.category
        v.tv(R.id.tvPrice).text = co.edu.compensar.tiendatech.data.Repo.money(p.price)
        v.setOnClickListener { onOpen(p) }
        v.findViewById<View>(R.id.btnAdd).setOnClickListener { onAdd(p) }
    }
