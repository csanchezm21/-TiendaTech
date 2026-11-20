
package co.edu.compensar.tiendatech.ui.auth

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import co.edu.compensar.tiendatech.R
import co.edu.compensar.tiendatech.data.*
import co.edu.compensar.tiendatech.databinding.*
import co.edu.compensar.tiendatech.ui.*
import kotlinx.coroutines.launch

class LoginFragment :
    VBFragment<FragmentLoginBinding>(FragmentLoginBinding::inflate) {

    // Repositorios que conectan con Firebase.
    private val authRepository = FirebaseAuthRepository()
    private val userRepository = FirebaseUserRepository()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val prefs = requireContext()
            .getSharedPreferences("tienda", Context.MODE_PRIVATE)

        // Iniciar sesión con correo y contraseña.
        b.btnLogin.setOnClickListener {

            val email = b.etEmail.text.toString().trim()
            val pass = b.etPassword.text.toString()

            b.tilEmail.error =
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
                    "Correo no válido" else null

            b.tilPassword.error =
                if (pass.length < 6)
                    "Mínimo 6 caracteres" else null

            if (b.tilEmail.error != null ||
                b.tilPassword.error != null
            ) return@setOnClickListener

            b.btnLogin.isEnabled = false

            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    // Firebase comprueba el correo y la contraseña.
                    val firebaseUser = authRepository
                        .iniciarSesion(email, pass)

                    if (firebaseUser == null) {
                        toast("No fue posible iniciar sesión")
                        return@launch
                    }

                    // Consultamos el perfil almacenado en Firestore.
                    val perfil = userRepository
                        .obtenerPerfil(firebaseUser.uid)

                    if (perfil == null) {
                        authRepository.cerrarSesion()
                        toast("No se encontró el perfil del usuario")
                        return@launch
                    }

                    // Comprobamos que la cuenta esté activa.
                    if (!perfil.active) {
                        authRepository.cerrarSesion()
                        toast("Esta cuenta se encuentra desactivada")
                        return@launch
                    }

                    // Convertimos el texto del rol al enum de Kotlin.
                    val rol = try {
                        Role.valueOf(perfil.role.uppercase())
                    } catch (e: IllegalArgumentException) {
                        authRepository.cerrarSesion()
                        toast("El rol del usuario no es válido")
                        return@launch
                    }

                    val user = User(
                        id = firebaseUser.uid.hashCode(),
                        name = perfil.name,
                        email = perfil.email,
                        password = "",
                        role = rol,
                        active = perfil.active,
                        firebaseUid = firebaseUser.uid
                    )

                    // Guardamos el último correo utilizado.
                    prefs.edit()
                        .putString("last_email", email)
                        .apply()

                    // Entramos al panel correspondiente al rol.
                    enter(user)

                } catch (e: Exception) {
                    toast("Error al iniciar sesión: ${e.localizedMessage}")
                } finally {
                    b.btnLogin.isEnabled = true
                }
            }
        }

        // La biometría se adaptará a la sesión de Firebase
        // después de comprobar el inicio de sesión principal.
        b.btnBiometric.setOnClickListener {
            toast("Primero verifica el inicio de sesión con Firebase")
        }

        b.tvForgot.setOnClickListener {
            (activity as AuthActivity).show(RecoverFragment(), true)
        }

        b.tvRegister.setOnClickListener {
            (activity as AuthActivity).show(RegisterFragment(), true)
        }
    }

    // Guarda el usuario y abre la actividad principal.
    private fun enter(user: User) {
        Repo.current = user
        (activity as AuthActivity).goMain()
    }
}
