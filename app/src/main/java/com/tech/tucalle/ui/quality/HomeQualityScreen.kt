package com.tech.tucalle.ui.quality

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.tech.tucalle.navigation.BottomNavigationBarDynamic
import com.tech.tucalle.ui.components.*
import com.tech.tucalle.ui.theme.Roboto
import com.tech.tucalle.ui.viewmodel.HomeViewModel

@Composable
fun HomeQualityScreen(
    navController: NavHostController,
    homeViewModel: HomeViewModel = viewModel(),
    onRestaurantClick: (String) -> Unit = {}
) {
    val direccionReal by homeViewModel.direccionActual.collectAsState()
    val context = LocalContext.current
    val bannersReales by homeViewModel.banners.collectAsState()
    val tiendasReales by homeViewModel.tiendasCercanas.collectAsState()
    val platosReales by homeViewModel.platosPopulares.collectAsState()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) homeViewModel.obtenerUbicacionReal(context)
    }

    LaunchedEffect(Unit) {
        val permiso = Manifest.permission.ACCESS_FINE_LOCATION
        if (ContextCompat.checkSelfPermission(context, permiso) == PackageManager.PERMISSION_GRANTED) {
            homeViewModel.obtenerUbicacionReal(context)
        } else {
            permissionLauncher.launch(permiso)
        }
    }

    Scaffold(
        containerColor = Color.White,
        bottomBar = {
            BottomNavigationBarDynamic(
                rol = "QUALITY",
                currentSelection = "Home",
                navController = navController
            )
        }
        // 🔥 ELIMINAMOS EL BOTÓN FLOTANTE (+) DEFINITIVAMENTE
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // ── SECCIÓN 1: CABECERA ──────────────────────────────
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    TopLocationBar(direccion = direccionReal)
                    Spacer(modifier = Modifier.height(20.dp))

                    if (bannersReales.isNotEmpty()) {
                        PromoCarousel(banners = bannersReales.map { it.imageUrl })
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.LightGray)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    CategoriesRow()
                    Spacer(modifier = Modifier.height(28.dp))

                    // Título con Roboto Bold — igual que HomeScreen usuario
                    Text(
                        text = "¿Qué se te antoja hoy?",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = Roboto,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    SearchBarUI()
                    Spacer(modifier = Modifier.height(28.dp))
                }
            }

            // ── SECCIÓN 2: POPULARES AHORA ───────────────────────
            item {
                Column(modifier = Modifier.padding(start = 20.dp)) {
                    SectionHeader(title = "Populares ahora")
                    Spacer(modifier = Modifier.height(12.dp))
                    LazyRow {
                        items(platosReales) { plato ->
                            val porcentaje = if (plato.precioOriginal > 0) {
                                ((1 - (plato.precioDescuento / plato.precioOriginal)) * 100).toInt()
                            } else 0
                            DishCard(
                                nombre = plato.nombre,
                                restaurante = plato.nombreTienda.ifBlank { "Huarique" },
                                calificacion = "%.1f".format(plato.calificacionPlato),
                                precioOriginal = "S/ ${"%.2f".format(plato.precioOriginal)}",
                                precioDescuento = "S/ ${"%.2f".format(plato.precioDescuento)}",
                                descuentoTag = "-$porcentaje%",
                                imagenUrl = plato.imagenUrl
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(30.dp))
                }
            }

            // ── SECCIÓN 3: HUARIQUES RECOMENDADOS ───────────────
            item {
                Column(modifier = Modifier.padding(start = 20.dp)) {
                    SectionHeader(title = "Huariques recomendados")
                    Spacer(modifier = Modifier.height(12.dp))
                    LazyRow {
                        items(tiendasReales) { tienda ->
                            RestaurantCard(
                                nombre = tienda.nombre,
                                distrito = tienda.obtenerDistrito(),
                                horario = tienda.horario,
                                calificacion = "%.1f".format(tienda.calificacionGeneral),
                                etiquetas = tienda.etiquetas,
                                portadaUrl = tienda.portadaUrl,
                                onClick = { onRestaurantClick(tienda.id) }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(30.dp))
                }
            }

            // ── SECCIÓN 4: LOS MÁS RECOMENDADOS ─────────────────
            item {
                Column(modifier = Modifier.padding(start = 20.dp)) {
                    Text(
                        "Los más recomendados",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = Roboto
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    LazyRow {
                        items(tiendasReales.take(6)) { tienda ->
                            RecommendedLogo(imageUrl = tienda.portadaUrl)
                        }
                    }
                    Spacer(modifier = Modifier.height(30.dp))
                }
            }

            // ── SECCIÓN 5: PORQUE LO BUENO SE REPITE ────────────
            item {
                Column(modifier = Modifier.padding(start = 20.dp)) {
                    Text(
                        "Porque lo bueno se repite",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = Roboto
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    LazyRow {
                        items(tiendasReales.reversed().take(3)) { tienda ->
                            RepeatCard(
                                imageUrl = tienda.portadaUrl,
                                title = tienda.nombre,
                                onClick = { onRestaurantClick(tienda.id) }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }
    }
}