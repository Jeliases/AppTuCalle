package com.tech.tucalle.ui.auth

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.Locale

@SuppressLint("MissingPermission")
@Composable
fun MapSelectorScreen(
    onLocationSelected: (direccion: String, latitud: Double, longitud: Double) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Ubicación por defecto centrado en Lima, Perú si no se activa el GPS
    val defaultLocation = LatLng(-12.046374, -77.042793)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 15f)
    }

    var textAddress by remember { mutableStateOf("Buscando ubicación...") }
    var currentLatLng by remember { mutableStateOf(defaultLocation) }
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    // Gestor de permisos para pedir acceso al GPS
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasLocationPermission = isGranted
        if (isGranted) {
            // Si concede el permiso, obtenemos de inmediato la ubicación actual para centrar el mapa
            val service = LocationServices.getFusedLocationProviderClient(context)
            service.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    val latLng = LatLng(it.latitude, it.longitude)
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(latLng, 17f)
                    currentLatLng = latLng
                }
            }
        }
    }

    // Pedir permiso al abrir el mapa si aún no está otorgado
    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            val service = LocationServices.getFusedLocationProviderClient(context)
            service.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    val latLng = LatLng(it.latitude, it.longitude)
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(latLng, 17f)
                    currentLatLng = latLng
                }
            }
        }
    }

    // Traducir coordenadas (Latitud, Longitud) a Dirección en texto usando el Geocoder de Android
    fun updateAddressFromCoordinates(latLng: LatLng) {
        coroutineScope.launch(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                withContext(Dispatchers.Main) {
                    if (!addresses.isNullOrEmpty()) {
                        val address = addresses[0]
                        // Formateamos la dirección de manera legible (Calle + Altura + Distrito)
                        val fullAddress = address.getAddressLine(0) ?: "Dirección sin nombre"
                        textAddress = fullAddress
                    } else {
                        textAddress = "No se encontró una dirección en este punto"
                    }
                }
            } catch (e: IOException) {
                withContext(Dispatchers.Main) {
                    textAddress = "Error al obtener la dirección por red"
                }
            }
        }
    }

    // Detectar cuando la cámara del mapa se detiene para actualizar la dirección
    LaunchedEffect(cameraPositionState.isMoving) {
        if (!cameraPositionState.isMoving) {
            val centerLatLng = cameraPositionState.position.target
            currentLatLng = centerLatLng
            updateAddressFromCoordinates(centerLatLng)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. Renderizado del mapa de Google
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                isMyLocationEnabled = hasLocationPermission // Muestra el puntito azul de mi ubicación si hay permisos
            ),
            uiSettings = MapUiSettings(
                myLocationButtonEnabled = true, // Botón nativo para re-centrar el GPS
                zoomControlsEnabled = false     // Ocultamos los botones de +/- feos para un diseño más limpio
            )
        )

        // 2. PIN / MARCADOR FIJO AL CENTRO (Estilo Rappi)
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = "Pin de ubicación",
                tint = Color(0xFFD32F2F),
                modifier = Modifier
                    .size(48.dp)
                    .offset(y = (-24).dp) // Compensa la altura del icono para apuntar exactamente con la base del pin
            )
        }

        // 3. TARJETA SUPERIOR: Muestra la dirección actual en texto
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.TopCenter),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = Color(0xFFD32F2F),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = textAddress,
                    fontSize = 14.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // 4. SECCIÓN INFERIOR: Botones de Confirmar / Cancelar
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.BottomCenter)
        ) {
            Button(
                onClick = {
                    onLocationSelected(textAddress, currentLatLng.latitude, currentLatLng.longitude)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                shape = RoundedCornerShape(25.dp)
            ) {
                Text(
                    text = "Confirmar ubicación de tienda",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray.copy(alpha = 0.8f)),
                shape = RoundedCornerShape(25.dp)
            ) {
                Text(
                    text = "Cancelar",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}