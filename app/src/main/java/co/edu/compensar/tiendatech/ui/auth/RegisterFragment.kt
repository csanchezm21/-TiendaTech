
package co.edu.compensar.tiendatech.ui.auth

import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.lifecycle.lifecycleScope
import co.edu.compensar.tiendatech.data.FirebaseAuthRepository
import co.edu.compensar.tiendatech.data.FirebaseUserRepository
import co.edu.compensar.tiendatech.data.Role
import co.edu.compensar.tiendatech.data.UserProfile
import co.edu.compensar.tiendatech.databinding.FragmentRegisterBinding
import co.edu.compensar.tiendatech.ui.VBFragment
import co.edu.compensar.tiendatech.ui.setItems
import co.edu.compensar.tiendatech.ui.toast
import kotlinx.coroutines.launch

class RegisterFragment :
    VBFragment<FragmentRegisterBinding>(FragmentRegisterBinding::inflate) {

    private val authRepository = FirebaseAuthRepository()
    private val userRepository = FirebaseUserRepository()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        b.actRole.setItems(
            listOf(Role.BUYER.label, Role.SELLER.label),
            Role.BUYER.label
        )

        b.tvBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        b.btnRegister.setOnClickListener {

            val name = b.etName.text.toString().trim()
            val email = b.etEmail.text.toString().trim()
            val pass = b.etPassword.text.toString()
            val confirm = b.etConfirm.text.toString()

            b.tilName.error =
                if (name.length < 3) "Ingresa tu nombre" else null

            b.tilEmail.error =
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
                    "Correo no válido" else null

            b.tilPassword.error =
                if (pass.length < 6) "Mínimo 6 caracteres" else null

            b.tilConfirm.error =
                if (pass != confirm) "Las contraseñas no coinciden" else null

            if (listOf(
                    b.tilName,
                    b.tilEmail,
                    b.tilPassword,
                    b.tilConfirm
                ).any { it.error != null }
            ) return@setOnClickListener

            val role = if (b.actRole.text.toString() == Role.SELLER.label) {
                Role.SELLER
            } else {
                Role.BUYER
            }

            b.btnRegister.isEnabled = false

            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    // Intentamos crear la cuenta en Firebase.
                    val firebaseUser = try {
                        authRepository.registrarUsuario(email, pass)
                    } catch (e: Exception) {
                        // Si ya existe, intentamos recuperar la cuenta.
                        authRepository.iniciarSesion(email, pass)
                    } ?: throw Exception("No se pudo autenticar al usuario")

                    // Solo creamos el perfil si todavía no existe.
                    if (!userRepository.existePerfil(firebaseUser.uid)) {

                        val perfil = UserProfile(
                            name = name,
                            email = email,
                            role = role.name,
                            active = true
                        )

                        userRepository.guardarPerfil(
                            firebaseUser.uid,
                            perfil
                        )
                    }

                    // Cerramos la sesión para regresar al formulario de ingreso.
                    authRepository.cerrarSesion()

                    toast("Cuenta y perfil verificados correctamente")
                    parentFragmentManager.popBackStack()

                } catch (e: Exception) {
                    toast(
                        "No se pudo completar el registro: " +
                                (e.localizedMessage ?: "Error desconocido")
                    )
                } finally {
                    b.btnRegister.isEnabled = true
                }
            }
        }
    }
}
