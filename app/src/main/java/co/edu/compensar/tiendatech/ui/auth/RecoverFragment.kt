
package co.edu.compensar.tiendatech.ui.auth

import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.lifecycle.lifecycleScope
import co.edu.compensar.tiendatech.data.FirebaseAuthRepository
import co.edu.compensar.tiendatech.databinding.FragmentRecoverBinding
import co.edu.compensar.tiendatech.ui.VBFragment
import co.edu.compensar.tiendatech.ui.toast
import kotlinx.coroutines.launch

class RecoverFragment :
    VBFragment<FragmentRecoverBinding>(FragmentRecoverBinding::inflate) {

    // Repositorio encargado de comunicarse con Firebase.
    private val authRepository = FirebaseAuthRepository()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Regresar a la pantalla anterior.
        b.tvBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // Enviar solicitud para recuperar la contraseña.
        b.btnSend.setOnClickListener {

            val email = b.etEmail.text.toString().trim()

            // Validamos que el correo tenga un formato correcto.
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                b.tilEmail.error = "Correo no válido"
                return@setOnClickListener
            }

            b.tilEmail.error = null
            b.btnSend.isEnabled = false

            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    // Firebase envía el enlace de recuperación.
                    authRepository.enviarRecuperacion(email)

                    toast(
                        "Si el correo está registrado, recibirás un enlace para restablecer tu contraseña"
                    )

                    parentFragmentManager.popBackStack()

                } catch (e: Exception) {
                    toast("No se pudo enviar el correo de recuperación")
                } finally {
                    b.btnSend.isEnabled = true
                }
            }
        }
    }
}
