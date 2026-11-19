
package co.edu.compensar.tiendatech.data

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

// Administra los perfiles de los usuarios en Firestore.
class FirebaseUserRepository {

    // Conexión con la base de datos de Firebase.
    private val db = FirebaseFirestore.getInstance()

    // Colección donde guardaremos los perfiles.
    private val usuarios = db.collection("usuarios")

    // Guarda el perfil utilizando el UID de Firebase como identificador.
    suspend fun guardarPerfil(
        uid: String,
        perfil: UserProfile
    ) {
        usuarios.document(uid).set(perfil).await()
    }

    // Consulta el perfil de un usuario mediante su UID.
    suspend fun obtenerPerfil(uid: String): UserProfile? {
        val documento = usuarios.document(uid).get().await()

        return documento.toObject(UserProfile::class.java)
    }
}
