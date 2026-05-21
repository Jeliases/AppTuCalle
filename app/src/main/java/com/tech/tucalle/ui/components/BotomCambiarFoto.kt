package com.tech.tucalle.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.tech.tucalle.domain.StorageService

@Composable
fun BotonCambiarFoto(
    rutaStorage: String,
    onFotoSubida: (String) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    var isUploading by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            isUploading = true
            StorageService.subirFoto(
                uri = uri,
                ruta = rutaStorage,
                onSuccess = { url ->
                    isUploading = false
                    onFotoSubida(url)
                },
                onFailure = {
                    isUploading = false
                    // Aquí podrías agregar un Toast de error si deseas
                }
            )
        }
    }

    Box(
        modifier = modifier.clickable {
            if (!isUploading) {
                launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
        },
        contentAlignment = Alignment.Center
    ) {
        content()
        // Muestra un circulito de carga encima de la foto mientras se sube
        if (isUploading) {
            CircularProgressIndicator(color = androidx.compose.ui.graphics.Color.White)
        }
    }
}