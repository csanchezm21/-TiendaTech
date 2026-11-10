package co.edu.compensar.tiendatech.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import co.edu.compensar.tiendatech.R
import co.edu.compensar.tiendatech.data.Repo
import co.edu.compensar.tiendatech.data.Role
import co.edu.compensar.tiendatech.databinding.ActivityMainBinding
import co.edu.compensar.tiendatech.ui.admin.*
import co.edu.compensar.tiendatech.ui.buyer.*
import co.edu.compensar.tiendatech.ui.seller.*
import co.edu.compensar.tiendatech.ui.shared.*

/** Activity principal: Toolbar + Bottom Navigation cuyo menú cambia según el rol. */
class MainActivity : AppCompatActivity() {
    private lateinit var b: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val user = Repo.current
        if (user == null) { logout(); return }

        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)
        setSupportActionBar(b.toolbar)

        // El título de la Toolbar lo define cada fragmento (interfaz Titled)
        supportFragmentManager.registerFragmentLifecycleCallbacks(object :
            FragmentManager.FragmentLifecycleCallbacks() {
            override fun onFragmentResumed(fm: FragmentManager, f: Fragment) {
                if (f is Titled) supportActionBar?.title = f.title
            }
        }, true)
        // Flecha "atrás" solo cuando hay pantallas apiladas (detalle, pago, mis compras)
        supportFragmentManager.addOnBackStackChangedListener {
            supportActionBar?.setDisplayHomeAsUpEnabled(supportFragmentManager.backStackEntryCount > 0)
        }

        b.bottomNav.inflateMenu(
            when (user.role) {
                Role.ADMIN -> R.menu.menu_admin
                Role.SELLER -> R.menu.menu_seller
                Role.BUYER -> R.menu.menu_buyer
            }
        )
        b.bottomNav.setOnItemSelectedListener { item ->
            supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            navigate(rootFragment(item.itemId), false)
            true
        }
        if (savedInstanceState == null) selectTab(b.bottomNav.menu.getItem(0).itemId)
    }

    private fun rootFragment(id: Int): Fragment = when (id) {
        R.id.nav_admin_panel -> AdminDashboardFragment()
        R.id.nav_admin_users -> UsersFragment()
        R.id.nav_admin_products, R.id.nav_seller_products -> ProductsFragment()
        R.id.nav_admin_report -> SalesReportFragment()
        R.id.nav_seller_orders -> OrdersFragment()
        R.id.nav_buyer_home -> HomeFragment()
        R.id.nav_buyer_catalog -> CatalogFragment()
        R.id.nav_buyer_cart -> CartFragment()
        else -> ProfileFragment()
    }

    fun navigate(f: Fragment, addToBackStack: Boolean = true) {
        val t = supportFragmentManager.beginTransaction().replace(R.id.container, f)
        if (addToBackStack) t.addToBackStack(null)
        t.commit()
    }

    fun selectTab(id: Int) { b.bottomNav.selectedItemId = id }

    override fun onSupportNavigateUp(): Boolean {
        supportFragmentManager.popBackStack(); return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu); return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_logout) { logout(); return true }
        return super.onOptionsItemSelected(item)
    }

    fun logout() {
        Repo.current = null
        Repo.cart.clear()
        startActivity(Intent(this, AuthActivity::class.java))
        finish()
    }
}
