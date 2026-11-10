package co.edu.compensar.tiendatech.ui.buyer

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.chip.Chip
import co.edu.compensar.tiendatech.R
import co.edu.compensar.tiendatech.data.*
import co.edu.compensar.tiendatech.databinding.*
import co.edu.compensar.tiendatech.ui.*

class CatalogFragment : VBFragment<FragmentCatalogBinding>(FragmentCatalogBinding::inflate), Titled {
    override val title = "Catálogo"
    private lateinit var adapter: SimpleAdapter<Product>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = productAdapter(emptyList(),
            onOpen = { main().navigate(ProductDetailFragment().apply { arguments = bundleOf("id" to it.id) }) },
            onAdd = { Repo.addToCart(it); toast("${it.name} agregado al carrito") })
        b.rvList.layoutManager = GridLayoutManager(requireContext(), 2)
        b.rvList.adapter = adapter

        val initial = arguments?.getString("category") ?: ""
        (listOf("") + Repo.categories).forEach { c ->
            b.chipGroup.addView(Chip(requireContext()).apply {
                text = if (c.isEmpty()) "Todos" else c
                tag = c
                isCheckable = true
                isChecked = (c == initial)
            })
        }
        b.chipGroup.setOnCheckedStateChangeListener { _, _ -> refresh() }
        b.etSearch.doAfterTextChanged { refresh() }
        refresh()
    }

    private fun refresh() {
        val q = b.etSearch.text.toString().trim()
        val cat = b.chipGroup.findViewById<Chip>(b.chipGroup.checkedChipId)?.tag as? String ?: ""
        val list = Repo.products.filter {
            (cat.isEmpty() || it.category == cat) && (q.isEmpty() || it.name.contains(q, true)) && it.stock > 0
        }
        adapter.update(list)
        b.tvEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
    }
}
