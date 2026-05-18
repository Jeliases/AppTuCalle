package com.tech.tucalle.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.tech.tucalle.data.PlatoDTO
import com.tech.tucalle.data.RecomendacionQualityDTO
import com.tech.tucalle.data.TiendaDTO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// Este es el estado que controlará la pantalla del Quality
data class QualityUiState(
    val tiendasDisponibles: List<TiendaDTO> = emptyList(),
    val tiendaSeleccionada: TiendaDTO? = null,
    val platosDeTienda: List<PlatoDTO> = emptyList(),

    // Aquí guardamos los IDs de todos los platos que escoja en el Dropdown
    val platosSeleccionadosIds: List<String> = emptyList(),

    // Las variables del método CHAS
    val confort: Double = 0.0,
    val higiene: Double = 0.0,
    val atencion: Double = 0.0,
    val sabrosura: Double = 0.0,

    val comentario: String = "",
    val isLoading: Boolean = false,
    val mensajeExito: String = ""
)

class QualityViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow(QualityUiState())
    val uiState: StateFlow<QualityUiState> = _uiState.asStateFlow()

    init {
        cargarTiendasParaEvaluar()
    }

    // 1. Cargamos las tiendas que ya fueron aprobadas por el Admin
    private fun cargarTiendasParaEvaluar() {
        db.collection("tiendas")
            .whereEqualTo("estado", "APROBADO")
            .get()
            .addOnSuccessListener { result ->
                val tiendas = result.documents.mapNotNull { doc ->
                    doc.toObject(TiendaDTO::class.java)?.copy(id = doc.id)
                }
                _uiState.update { it.copy(tiendasDisponibles = tiendas) }
            }
            .addOnFailureListener { Log.e("QualityVM", "Error al cargar tiendas", it) }
    }

    // 2. Cuando el Quality elige una tienda, buscamos sus platos
    fun seleccionarTienda(tienda: TiendaDTO) {
        _uiState.update { it.copy(
            tiendaSeleccionada = tienda,
            platosSeleccionadosIds = emptyList(), // Reseteamos si cambia de tienda
            confort = 0.0, higiene = 0.0, atencion = 0.0, sabrosura = 0.0, comentario = ""
        ) }
        cargarPlatosDeTienda(tienda.id)
    }

    private fun cargarPlatosDeTienda(idTienda: String) {
        db.collection("platos")
            .whereEqualTo("idTienda", idTienda)
            .whereEqualTo("estado", "APROBADO")
            .get()
            .addOnSuccessListener { result ->
                val platos = result.documents.mapNotNull { doc ->
                    doc.toObject(PlatoDTO::class.java)?.copy(id = doc.id)
                }
                _uiState.update { it.copy(platosDeTienda = platos) }
            }
    }

    // 3. Lógica para el Dropdown Múltiple (Agregar o quitar platos)
    fun togglePlato(idPlato: String) {
        val actuales = _uiState.value.platosSeleccionadosIds.toMutableList()
        if (actuales.contains(idPlato)) {
            actuales.remove(idPlato) // Si ya lo había tocado, lo desmarca
        } else {
            actuales.add(idPlato) // Si no, lo agrega a la recomendación
        }
        _uiState.update { it.copy(platosSeleccionadosIds = actuales) }
    }

    // Actualizar valores de los Sliders (Estrellitas)
    fun onChasChange(c: Double, h: Double, a: Double, s: Double) {
        _uiState.update { it.copy(confort = c, higiene = h, atencion = a, sabrosura = s) }
    }

    fun onComentarioChange(v: String) = _uiState.update { it.copy(comentario = v) }

    // 4. Enviar la reseña final a Firebase
    fun enviarRecomendacion() {
        val uid = auth.currentUser?.uid ?: return
        val state = _uiState.value

        if (state.tiendaSeleccionada == null || state.platosSeleccionadosIds.isEmpty()) {
            _uiState.update { it.copy(mensajeExito = "Selecciona una tienda y al menos 1 plato") }
            return
        }

        _uiState.update { it.copy(isLoading = true) }

        val nuevaResena = RecomendacionQualityDTO(
            idQuality = uid,
            idTienda = state.tiendaSeleccionada.id,
            chas = mapOf(
                "Confort" to state.confort,
                "Higiene" to state.higiene,
                "Atencion" to state.atencion,
                "Sabrosura" to state.sabrosura
            ),
            platosRecomendados = state.platosSeleccionadosIds, // La lista completa va a Firebase
            comentario = state.comentario
        )

        db.collection("resenas_quality").add(nuevaResena)
            .addOnSuccessListener {
                _uiState.update { it.copy(
                    isLoading = false,
                    mensajeExito = "¡Evaluación CHAS enviada con éxito!",
                    // Limpiamos los campos tras el éxito
                    platosSeleccionadosIds = emptyList(),
                    confort = 0.0, higiene = 0.0, atencion = 0.0, sabrosura = 0.0, comentario = ""
                ) }
            }
            .addOnFailureListener {
                _uiState.update { it.copy(isLoading = false, mensajeExito = "Error de red") }
            }
    }
}
