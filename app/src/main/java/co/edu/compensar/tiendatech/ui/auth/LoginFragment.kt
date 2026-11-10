package co.edu.compensar.tiendatech.ui.auth

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import co.edu.compensar.tiendatech.R
import co.edu.compensar.tiendatech.data.*
import co.edu.compensar.tiendatech.databinding.*
import co.edu.compensar.tiendatech.ui.*

class LoginFragment : VBFragment<FragmentLoginBinding>(FragmentLoginBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val prefs = requireContext().getSharedPreferences("tienda", Context.MODE_PRIVATE)

        b.btnLogin.setOnClickListener {
            val email = b.etEmail.text.toString().trim()
            val pass = b.etPassword.text.toString()
            b.tilEmail.error = if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) "Correo no válido" else null
            b.tilPassword.error = if (pass.length < 6) "Mínimo 6 caracteres" else null
            if (b.tilEmail.error != null || b.tilPassword.error != null) return@setOnClickListener

            val user = Repo.login(email, pass)
            if (user == null) { toast("Credenciales incorrectas o cuenta desactivada"); return@setOnClickListener }
            prefs.edit().putString("last_email", email).apply()
            enter(user)
        }
        b.btnBiometric.setOnClickListener { biometricLogin(prefs) }
        b.tvForgot.setOnClickListener { (activity as AuthActivity).show(RecoverFragment(), true) }
        b.tvRegister.setOnClickListener { (activity as AuthActivity).show(RegisterFragment(), true) }
    }

    private fun enter(user: User) {
        Repo.current = user
        (activity as AuthActivity).goMain()
    }

    private fun biometricLogin(prefs: SharedPreferences) {
        val last = prefs.getString("last_email", null)
            ?.let { e -> Repo.users.firstOrNull { it.email == e && it.active } }
        if (last == null) { toast("Inicia sesión una vez con tu contraseña para activar la huella"); return }

        val can = BiometricManager.from(requireContext())
            .canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK)
        if (can != BiometricManager.BIOMETRIC_SUCCESS) {
            toast("Este dispositivo no tiene huella o biometría configurada"); return
        }
        val prompt = BiometricPrompt(this, ContextCompat.getMainExecutor(requireContext()),
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) { enter(last) }
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) { toast(errString.toString()) }
            })
        prompt.authenticate(
            BiometricPrompt.PromptInfo.Builder()
                .setTitle("Ingreso con huella")
                .setSubtitle("Confirma tu identidad para entrar a TiendaTech")
                .setNegativeButtonText("Cancelar")
                .build()
        )
    }
}
