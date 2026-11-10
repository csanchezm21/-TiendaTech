package co.edu.compensar.tiendatech.ui.admin

import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import co.edu.compensar.tiendatech.R
import co.edu.compensar.tiendatech.data.*
import co.edu.compensar.tiendatech.databinding.*
import co.edu.compensar.tiendatech.ui.*

/** CRUD de usuarios (listado, creación, edición y eliminación). */
class UsersFragment : VBFragment<FragmentListBinding>(FragmentListBinding::inflate), Titled {
    override val title = "Gestión de usuarios"
    private lateinit var adapter: SimpleAdapter<User>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        b.tilSearch.hint = "Buscar por nombre o correo"
        adapter = SimpleAdapter(emptyList(), R.layout.item_user) { v, u ->
            v.tv(R.id.tvName).text = u.name
            v.tv(R.id.tvEmail).text = u.email
            v.tv(R.id.tvRole).text = u.role.label
            v.tv(R.id.tvStatus).apply {
                text = if (u.active) "Activo" else "Inactivo"
                setTextColor(ContextCompat.getColor(context, if (u.active) R.color.success else R.color.coral))
            }
            v.findViewById<View>(R.id.btnEdit).setOnClickListener { showDialog(u) }
            v.findViewById<View>(R.id.btnDelete).setOnClickListener { confirmDelete(u) }
        }
        b.rvList.layoutManager = LinearLayoutManager(requireContext())
        b.rvList.adapter = adapter
        b.etSearch.doAfterTextChanged { refresh() }
        b.fabAdd.setOnClickListener { showDialog(null) }
        refresh()
    }

    private fun refresh() {
        val q = b.etSearch.text.toString().trim()
        val list = Repo.users.filter { q.isEmpty() || it.name.contains(q, true) || it.email.contains(q, true) }
        adapter.update(list)
        b.tvEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun confirmDelete(u: User) {
        if (u.id == Repo.current?.id) { toast("No puedes eliminar tu propia cuenta"); return }
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Eliminar usuario")
            .setMessage("¿Seguro que deseas eliminar a ${u.name}?")
            .setPositiveButton("Eliminar") { _, _ -> Repo.users.remove(u); refresh() }
            .setNegativeButton("Cancelar", null).show()
    }

    private fun showDialog(u: User?) {
        val d = DialogUserBinding.inflate(layoutInflater)
        d.actRole.setItems(Role.values().map { it.label }, (u?.role ?: Role.BUYER).label)
        if (u != null) {
            d.etName.setText(u.name); d.etEmail.setText(u.email)
            d.etPassword.setText(u.password); d.swActive.isChecked = u.active
        } else d.swActive.isChecked = true

        val dlg = MaterialAlertDialogBuilder(requireContext())
            .setTitle(if (u == null) "Nuevo usuario" else "Editar usuario")
            .setView(d.root)
            .setPositiveButton("Guardar", null)
            .setNegativeButton("Cancelar", null).create()
        dlg.show()
        dlg.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val name = d.etName.text.toString().trim()
            val email = d.etEmail.text.toString().trim()
            val pass = d.etPassword.text.toString()
            if (name.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches() || pass.length < 6) {
                toast("Completa nombre, correo válido y contraseña (mín. 6)"); return@setOnClickListener
            }
            if (Repo.emailExists(email, u?.id ?: -1)) { toast("Ese correo ya existe"); return@setOnClickListener }
            val role = Role.values().firstOrNull { it.label == d.actRole.text.toString() } ?: Role.BUYER
            if (u == null) Repo.addUser(name, email, pass, role)
            else { u.name = name; u.email = email; u.password = pass; u.role = role; u.active = d.swActive.isChecked }
            refresh(); dlg.dismiss()
        }
    }
}
