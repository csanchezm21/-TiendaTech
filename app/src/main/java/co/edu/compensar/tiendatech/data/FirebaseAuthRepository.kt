
package co.edu.compensar.tiendatech.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

// Administra las operaciones de autenticación de Firebase.
class FirebaseAuthRepository {

    // Instancia de Firebase Authentication.
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    // Registra un usuario con correo y contraseña.
    suspend fun registrarUsuario(
        correo: String,
        contrasena: String
    ): FirebaseUser? {

        val resultado = firebaseAuth
            .createUserWithEmailAndPassword(correo, contrasena)
            .await()

        return resultado.user
    }

    // Inicia sesión con correo y contraseña.
    suspend fun iniciarSesion(
        correo: String,
        contrasena: String
    ): FirebaseUser? {

        val resultado = firebaseAuth
            .signInWithEmailAndPassword(correo, contrasena)
            .await()

        return resultado.user
    }

    // Cierra la sesión del usuario actual.
    fun cerrarSesion() {
        firebaseAuth.signOut()
    }

    // Consulta qué usuario tiene la sesión iniciada.
    fun obtenerUsuarioActual(): FirebaseUser? {
        return firebaseAuth.currentUser
    }
}

