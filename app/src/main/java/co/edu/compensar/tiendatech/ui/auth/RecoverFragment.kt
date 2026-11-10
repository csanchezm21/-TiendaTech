package co.edu.compensar.tiendatech.ui.auth

import android.os.Bundle
import android.util.Patterns
import android.view.View
import co.edu.compensar.tiendatech.R
import co.edu.compensar.tiendatech.data.*
import co.edu.compensar.tiendatech.databinding.*
import co.edu.compensar.tiendatech.ui.*

class RecoverFragment : VBFragment<FragmentRecoverBinding>(FragmentRecoverBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        b.tvBack.setOnClickListener { parentFragmentManager.popBackStack() }
        b.btnSend.setOnClickListener {
            val email = b.etEmail.text.toString().trim()
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                b.tilEmail.error = "Correo no válido"; return@setOnClickListener
            }
            b.tilEmail.error = null
            // Por seguridad no se revela si el correo existe o no
            toast("Si el correo está registrado, recibirás un enlace para restablecer tu contraseña")
            parentFragmentManager.popBackStack()
        }
    }
}
