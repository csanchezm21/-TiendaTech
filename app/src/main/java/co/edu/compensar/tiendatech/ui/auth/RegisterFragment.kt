package co.edu.compensar.tiendatech.ui.auth

import android.os.Bundle
import android.util.Patterns
import android.view.View
import co.edu.compensar.tiendatech.R
import co.edu.compensar.tiendatech.data.*
import co.edu.compensar.tiendatech.databinding.*
import co.edu.compensar.tiendatech.ui.*

class RegisterFragment : VBFragment<FragmentRegisterBinding>(FragmentRegisterBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        b.actRole.setItems(listOf(Role.BUYER.label, Role.SELLER.label), Role.BUYER.label)
        b.tvBack.setOnClickListener { parentFragmentManager.popBackStack() }

        b.btnRegister.setOnClickListener {
            val name = b.etName.text.toString().trim()
            val email = b.etEmail.text.toString().trim()
            val pass = b.etPassword.text.toString()
            b.tilName.error = if (name.length < 3) "Ingresa tu nombre" else null
            b.tilEmail.error = when {
                !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Correo no válido"
                Repo.emailExists(email) -> "Este correo ya está registrado"
                else -> null
            }
            b.tilPassword.error = if (pass.length < 6) "Mínimo 6 caracteres" else null
            b.tilConfirm.error = if (pass != b.etConfirm.text.toString()) "Las contraseñas no coinciden" else null
            if (listOf(b.tilName, b.tilEmail, b.tilPassword, b.tilConfirm).any { it.error != null }) return@setOnClickListener

            val role = if (b.actRole.text.toString() == Role.SELLER.label) Role.SELLER else Role.BUYER
            Repo.addUser(name, email, pass, role)
            toast("Cuenta creada. Ya puedes iniciar sesión")
            parentFragmentManager.popBackStack()
        }
    }
}
