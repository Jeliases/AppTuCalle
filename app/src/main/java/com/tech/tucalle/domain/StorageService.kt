package com.tech.tucalle.domain

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage

object StorageService {
    private val storage = FirebaseStorage.getInstance()

    fun subirFoto(
        uri: Uri,
        ruta: String,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val ref = storage.reference.child(ruta)
        ref.putFile(uri)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { downloadUri ->
                    onSuccess(downloadUri.toString())
                }
            }
            .addOnFailureListener { e -> onFailure(e) }
    }
}