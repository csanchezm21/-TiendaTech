
package co.edu.compensar.tiendatech.ui.admin

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import co.edu.compensar.tiendatech.R
import co.edu.compensar.tiendatech.data.*
import co.edu.compensar.tiendatech.databinding.*
import co.edu.compensar.tiendatech.ui.*
import kotlinx.coroutines.launch

// Pantalla para consultar y administrar los perfiles de usuarios.
class UsersFragment :
    VBFragment<FragmentListBinding>(FragmentListBinding::inflate), Titled {

    override val title = "Gestión de usuarios"

    private lateinit var adapter: SimpleAdapter<User>

    // Repositorio que se comunica con Firestore.
    private val userRepository = FirebaseUserRepository()

    // Lista de usuarios cargados desde Firebase.
    private var listaUsuarios = listOf<User>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        b.tilSearch.hint = "Buscar por nombre o correo"

        // Configuramos la lista visual de usuarios.
        adapter = SimpleAdapter(emptyList(), R.layout.item_user) { v, u ->

            v.tv(R.id.tvName).text = u.name
            v.tv(R.id.tvEmail).text = u.email
            v.tv(R.id.tvRole).text = u.role.label

            v.tv(R.id.tvStatus).apply {
                text = if (u.active) "Activo" else "Inactivo"

                setTextColor(
                    ContextCompat.getColor(
                        context,
                        if (u.active) R.color.success else R.color.coral
                    )
                )
            }

            // Abrir formulario para editar el usuario.
            v.findViewById<View>(R.id.btnEdit).setOnClickListener {
                showDialog(u)
            }

            // La eliminación de cuentas requiere un servicio seguro.
            v.findViewById<View>(R.id.btnDelete).setOnClickListener {
                toast("La eliminación de cuentas se implementará mediante un servicio seguro")
            }
        }

        b.rvList.layoutManager = LinearLayoutManager(requireContext())
        b.rvList.adapter = adapter

        // Filtramos los usuarios mientras se escribe.
        b.etSearch.doAfterTextChanged {
            refresh()
        }

        // La creación de cuentas requiere un servicio seguro.
        b.fabAdd.setOnClickListener {
            toast("La creación de cuentas se implementará mediante un servicio seguro")
        }

        // Consultamos los usuarios desde Firestore.
        cargarUsuarios()
    }

    // Consulta los perfiles guardados en Firebase.
    private fun cargarUsuarios() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                listaUsuarios = userRepository.obtenerUsuarios()
                refresh()

            } catch (e: Exception) {
                toast("No se pudieron consultar los usuarios: ${e.localizedMessage}")
            }
        }
    }

    // Actualiza la lista y aplica el filtro de búsqueda.
    private fun refresh() {
        val q = b.etSearch.text.toString().trim()

        val listaFiltrada = listaUsuarios.filter {
            q.isEmpty() ||
                    it.name.contains(q, true) ||
                    it.email.contains(q, true)
        }

        adapter.update(listaFiltrada)

        b.tvEmpty.visibility =
            if (listaFiltrada.isEmpty()) View.VISIBLE else View.GONE
    }

    // Muestra el formulario para modificar un perfil.
    private fun showDialog(u: User) {

        val d = DialogUserBinding.inflate(layoutInflater)

        d.actRole.setItems(
            listOf(Role.BUYER.label, Role.SELLER.label),
            u.role.label
        )

        d.etName.setText(u.name)
        d.etEmail.setText(u.email)

        // El correo y la contraseña no se modifican desde esta pantalla.
        d.etEmail.isEnabled = false
        d.etPassword.visibility = View.GONE

        // Mostramos el estado actual.
        d.swActive.isChecked = u.active

        val dlg = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Editar usuario")
            .setView(d.root)
            .setPositiveButton("Guardar", null)
            .setNegativeButton("Cancelar", null)
            .create()

        dlg.show()

        dlg.getButton(-1).setOnClickListener {

            val nombre = d.etName.text.toString().trim()

            if (nombre.length < 3) {
                toast("El nombre debe tener al menos 3 caracteres")
                return@setOnClickListener
            }

            val uid = u.firebaseUid

            if (uid.isNullOrBlank()) {
                toast("No se encontró el identificador de Firebase")
                return@setOnClickListener
            }

            val rol = Role.values().firstOrNull {
                it.label == d.actRole.text.toString()
            } ?: Role.BUYER

            val activo = d.swActive.isChecked

            // Guardamos los cambios en Firestore.
            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    userRepository.actualizarPerfil(
                        uid = uid,
                        nombre = nombre,
                        rol = rol,
                        activo = activo
                    )

                    toast("Usuario actualizado correctamente")
                    dlg.dismiss()

                    // Volvemos a consultar los datos actualizados.
                    cargarUsuarios()

                } catch (e: Exception) {
                    toast("No se pudo actualizar el usuario: ${e.localizedMessage}")
                }
            }
        }
    }
}
