package co.edu.compensar.tiendatech.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import co.edu.compensar.tiendatech.R
import co.edu.compensar.tiendatech.ui.auth.LoginFragment

/** Contenedor de Login, Registro y Recuperación de contraseña. */
class AuthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        if (savedInstanceState == null) show(LoginFragment(), false)
    }

    fun show(f: Fragment, addToBackStack: Boolean) {
        val t = supportFragmentManager.beginTransaction().replace(R.id.authContainer, f)
        if (addToBackStack) t.addToBackStack(null)
        t.commit()
    }

    fun goMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
