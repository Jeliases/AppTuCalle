package com.tech.tucalle.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.tech.tucalle.domain.model.ComentarioTienda
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ComentariosViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _comentarios = MutableStateFlow<List<ComentarioTienda>>(emptyList())
    val comentarios: StateFlow<List<ComentarioTienda>> = _comentarios.asStateFlow()

    fun cargarComentarios(idTienda: String) {
        db.collection("comentarios")
            .whereEqualTo("idTienda", idTienda)
            // .orderBy("fecha", com.google.firebase.firestore.Query.Direction.DESCENDING) // Opcional
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val lista = snapshot.toObjects(ComentarioTienda::class.java).sortedByDescending { it.fecha }
                    _comentarios.value = lista
                }
            }
    }

    fun enviarComentario(idTienda: String, nombreTienda: String, texto: String) {
        val uid = auth.currentUser?.uid ?: return

        // Buscamos si es un Usuario normal o un Quality para asignarle su rol visual
        db.collection("usuarios").document(uid).get().addOnSuccessListener { doc ->
            if (doc.exists()) {
                guardar(idTienda, nombreTienda, uid, doc.getString("nombre") ?: "Usuario", doc.getString("fotoUrl") ?: "", "USUARIO", texto)
            } else {
                db.collection("qualities").document(uid).get().addOnSuccessListener { qDoc ->
                    guardar(idTienda, nombreTienda, uid, qDoc.getString("nombre") ?: "Quality", qDoc.getString("fotoUrl") ?: "", "QUALITY", texto)
                }
            }
        }
    }

    private fun guardar(idTienda: String, nombreTienda: String, uid: String, nombre: String, foto: String, rol: String, texto: String) {
        val docRef = db.collection("comentarios").document()
        val nuevoComentario = ComentarioTienda(
            id = docRef.id,
            idTienda = idTienda,
            nombreTienda = nombreTienda,
            idUsuario = uid,
            nombreUsuario = nombre,
            fotoUsuario = foto,
            rolUsuario = rol,
            texto = texto
        )
        docRef.set(nuevoComentario)
    }

    fun toggleLike(idComentario: String) {
        val uid = auth.currentUser?.uid ?: return
        val docRef = db.collection("comentarios").document(idComentario)

        db.runTransaction { transaction ->
            val snapshot = transaction.get(docRef)
            val likedBy = snapshot.get("likedBy") as? List<String> ?: emptyList()
            val currentLikes = snapshot.getLong("likes")?.toInt() ?: 0

            if (likedBy.contains(uid)) {
                // Quitar Like
                transaction.update(docRef, "likedBy", FieldValue.arrayRemove(uid))
                transaction.update(docRef, "likes", currentLikes - 1)
            } else {
                // Dar Like
                transaction.update(docRef, "likedBy", FieldValue.arrayUnion(uid))
                transaction.update(docRef, "likes", currentLikes + 1)
            }
        }
    }

    fun obtenerUidActual(): String {
        return auth.currentUser?.uid ?: ""
    }
}