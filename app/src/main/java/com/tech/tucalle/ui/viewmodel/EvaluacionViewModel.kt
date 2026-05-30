package com.tech.tucalle.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.tech.tucalle.domain.model.ComentarioTienda
import com.tech.tucalle.domain.model.Plato
import com.tech.tucalle.domain.model.Store
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class EvaluacionViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _tiendasGuardadas = MutableStateFlow<List<Store>>(emptyList())
    val tiendasGuardadas: StateFlow<List<Store>> = _tiendasGuardadas.asStateFlow()

    private val _platosTienda = MutableStateFlow<List<Plato>>(emptyList())
    val platosTienda: StateFlow<List<Plato>> = _platosTienda.asStateFlow()

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting.asStateFlow()

    init { cargarTiendasGuardadas() }

    private fun cargarTiendasGuardadas() {
        val uid = auth.currentUser?.uid ?: return
        db.collection("qualities").document(uid).get().addOnSuccessListener { doc ->
            val misHuariquesIds = doc.get("misHuariques") as? List<String> ?: emptyList()
            if (misHuariquesIds.isNotEmpty()) {
                db.collection("tiendas").whereIn("uid", misHuariquesIds).get().addOnSuccessListener { snapshot ->
                    _tiendasGuardadas.value = snapshot.toObjects(Store::class.java)
                }
            }
        }
    }

    fun cargarPlatosDeTienda(idTienda: String) {
        if (idTienda.isBlank()) {
            _platosTienda.value = emptyList()
            return
        }
        db.collection("platos").whereEqualTo("idTienda", idTienda).get().addOnSuccessListener { snapshot ->
            _platosTienda.value = snapshot.toObjects(Plato::class.java)
        }
    }

    fun publicarEvaluacion(
        tiendaSeleccionada: Store,
        platosSeleccionados: List<String>,
        confort: Float, higiene: Float, atencion: Float, sabrosura: Float,
        comentario: String,
        onSuccess: () -> Unit
    ) {
        val uid = auth.currentUser?.uid ?: return
        _isSubmitting.value = true

        val promedioChas = (confort + higiene + atencion + sabrosura) / 4.0

        db.collection("qualities").document(uid).get().addOnSuccessListener { doc ->
            val nombreQ = doc.getString("nombre") ?: "Quality"
            val fotoQ = doc.getString("fotoUrl") ?: ""

            val docRef = db.collection("comentarios").document()
            val evaluacion = ComentarioTienda(
                id = docRef.id,
                idTienda = tiendaSeleccionada.uid,
                nombreTienda = tiendaSeleccionada.nombre,
                idUsuario = uid,
                nombreUsuario = nombreQ,
                fotoUsuario = fotoQ,
                rolUsuario = "QUALITY",
                texto = comentario,
                calificacion = promedioChas,
                platosSugeridos = platosSeleccionados // 🔥 Guardamos los platos
            )

            docRef.set(evaluacion).addOnSuccessListener {
                // Opcional: Actualizar el promedio general de la tienda aquí
                _isSubmitting.value = false
                onSuccess()
            }
        }
    }
}