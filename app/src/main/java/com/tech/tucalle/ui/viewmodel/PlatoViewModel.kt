package com.tech.tucalle.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.tech.tucalle.domain.model.Plato
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class PlatoViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val _platos = MutableStateFlow<List<Plato>>(emptyList())
    val platos = _platos.asStateFlow()

    fun cargarPlatos(idTienda: String) {
        db.collection("platos")
            .whereEqualTo("idTienda", idTienda)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    _platos.value = snapshot.toObjects(Plato::class.java)
                }
            }
    }

    fun guardarPlato(plato: Plato, onResult: (Boolean) -> Unit) {
        // 🔥 CORREGIDO: Usamos plato.uid en lugar de plato.id
        val docRef = if (plato.uid.isEmpty()) {
            db.collection("platos").document()
        } else {
            db.collection("platos").document(plato.uid)
        }

        // 🔥 CORREGIDO: Copiamos asignando el valor a 'uid'
        val platoConId = plato.copy(uid = docRef.id)

        docRef.set(platoConId)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }
}