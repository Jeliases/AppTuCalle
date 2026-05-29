package com.tech.tucalle.ui.auth

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Geocoder
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
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
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.Locale

@SuppressLint("MissingPermission")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapSelectorScreen(
    onLocationSelected: (direccion: String, latitud: Double, longitud: Double) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val defaultLocation = LatLng(-12.046374, -77.042793)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 15f)
    }

    var textAddress by remember { mutableStateOf("Buscando ubicación...") }
    var searchQuery by remember { mutableStateOf("") }
    var currentLatLng by remember { mutableStateOf(defaultLocation) }
    var hasLocationPermission by remember {
        mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
    }

    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        hasLocationPermission = isGranted
        if (isGranted) {
            val service = LocationServices.getFusedLocationProviderClient(context)
            service.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    val latLng = LatLng(it.latitude, it.longitude)
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(latLng, 17f)
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            val service = LocationServices.getFusedLocationProviderClient(context)
            service.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    val latLng = LatLng(it.latitude, it.longitude)
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(latLng, 17f)
                }
            }
        }
    }

    fun searchLocationFromText(query: String) {
        if (query.isEmpty()) return
        coroutineScope.launch(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocationName(query, 1)
                if (!addresses.isNullOrEmpty()) {
                    val address = addresses[0]
                    val latLng = LatLng(address.latitude, address.longitude)
                    withContext(Dispatchers.Main) {
                        cameraPositionState.position = CameraPosition.fromLatLngZoom(latLng, 17f)
                    }
                }
            } catch (e: Exception) {
                // Manejar error silenciosamente
            }
        }
    }

    fun updateAddressFromCoordinates(latLng: LatLng) {
        coroutineScope.launch(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                withContext(Dispatchers.Main) {
                    if (!addresses.isNullOrEmpty()) {
                        textAddress = addresses[0].getAddressLine(0) ?: "Dirección sin nombre"
                    } else {
                        textAddress = "No se encontró una dirección"
                    }
                }
            } catch (e: IOException) {
                withContext(Dispatchers.Main) { textAddress = "Error al conectar con GPS" }
            }
        }
    }

    LaunchedEffect(cameraPositionState.isMoving) {
        if (!cameraPositionState.isMoving) {
            val centerLatLng = cameraPositionState.position.target
            currentLatLng = centerLatLng
            updateAddressFromCoordinates(centerLatLng)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = hasLocationPermission),
            uiSettings = MapUiSettings(myLocationButtonEnabled = true, zoomControlsEnabled = false),
            // 🔥 MODIFICACIÓN AQUÍ: Se inyecta el Map ID recién creado
            googleMapOptionsFactory = {
                GoogleMapOptions().mapId("500f9cc7e1a79fd05e9402e0")
            }
        )

        Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = "Pin",
            tint = Color(0xFFD32F2F),
            modifier = Modifier.size(48.dp).align(Alignment.Center).offset(y = (-24).dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp).align(Alignment.TopCenter),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Buscar distrito o calle...") },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        IconButton(onClick = { searchLocationFromText(searchQuery) }) {
                            Icon(Icons.Default.Search, contentDescription = "Buscar", tint = Color(0xFFD32F2F))
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFD32F2F), unfocusedBorderColor = Color.LightGray
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFFD32F2F), modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = textAddress, fontSize = 14.sp, color = Color.DarkGray, maxLines = 2)
                }
            }
        }

        Column(modifier = Modifier.fillMaxWidth().padding(16.dp).align(Alignment.BottomCenter)) {
            Button(
                onClick = { onLocationSelected(textAddress, currentLatLng.latitude, currentLatLng.longitude) },
                modifier = Modifier.fillMaxWidth().height(55.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
            ) { Text("Confirmar ubicación", fontSize = 16.sp, fontWeight = FontWeight.Bold) }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth().height(55.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
            ) { Text("Cancelar", fontSize = 16.sp, fontWeight = FontWeight.Bold) }
        }
    }
}