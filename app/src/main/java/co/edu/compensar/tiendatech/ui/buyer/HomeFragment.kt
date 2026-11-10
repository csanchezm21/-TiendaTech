package co.edu.compensar.tiendatech.ui.buyer

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.chip.Chip
import co.edu.compensar.tiendatech.R
import co.edu.compensar.tiendatech.data.*
import co.edu.compensar.tiendatech.databinding.*
import co.edu.compensar.tiendatech.ui.*

class HomeFragment : VBFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate), Titled {
    override val title = "TiendaTech"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        b.tvGreeting.text = "Hola, ${Repo.current?.name?.split(" ")?.first()} 👋"
        b.btnCatalog.setOnClickListener { main().selectTab(R.id.nav_buyer_catalog) }

        Repo.categories.forEach { c ->
            b.chipCats.addView(Chip(requireContext()).apply {
                text = c
                setOnClickListener {
                    main().navigate(CatalogFragment().apply { arguments = bundleOf("category" to c) })
                }
            })
        }
        b.rvFeatured.layoutManager = GridLayoutManager(requireContext(), 2)
        b.rvFeatured.adapter = productAdapter(Repo.products.take(4),
            onOpen = { main().navigate(ProductDetailFragment().apply { arguments = bundleOf("id" to it.id) }) },
            onAdd = { Repo.addToCart(it); toast("${it.name} agregado al carrito") })
    }
}
