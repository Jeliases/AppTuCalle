package com.tech.tucalle.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.tech.tucalle.domain.model.Plato
import com.tech.tucalle.domain.model.Store
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class StoreDetailViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _tienda = MutableStateFlow<Store?>(null)
    val tienda: StateFlow<Store?> = _tienda.asStateFlow()

    private val _platos = MutableStateFlow<List<Plato>>(emptyList())
    val platos: StateFlow<List<Plato>> = _platos.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // 🔥 Estado para saber si el Quality guardó este huarique
    private val _isGuardado = MutableStateFlow(false)
    val isGuardado: StateFlow<Boolean> = _isGuardado.asStateFlow()

    fun cargarDatosTienda(idTienda: String) {
        if (idTienda.isBlank()) return
        _isLoading.value = true

        db.collection("tiendas").document(idTienda).get().addOnSuccessListener { document ->
            if (document.exists()) {
                _tienda.value = document.toObject(Store::class.java)?.copy(uid = document.id)
            }
        }

        db.collection("platos").whereEqualTo("idTienda", idTienda).whereEqualTo("estado", "APROBADO")
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) _platos.value = snapshot.toObjects(Plato::class.java)
                _isLoading.value = false
            }

        verificarSiEstaGuardado(idTienda)
    }

    private fun verificarSiEstaGuardado(idTienda: String) {
        val uid = auth.currentUser?.uid ?: return
        db.collection("qualities").document(uid).addSnapshotListener { doc, _ ->
            if (doc != null && doc.exists()) {
                val misHuariques = doc.get("misHuariques") as? List<String> ?: emptyList()
                _isGuardado.value = misHuariques.contains(idTienda)
            }
        }
    }

    fun toggleGuardarTienda(idTienda: String) {
        val uid = auth.currentUser?.uid ?: return
        val docRef = db.collection("qualities").document(uid)

        if (_isGuardado.value) {
            docRef.update("misHuariques", FieldValue.arrayRemove(idTienda))
        } else {
            docRef.update("misHuariques", FieldValue.arrayUnion(idTienda))
        }
    }
}