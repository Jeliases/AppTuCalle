package com.tech.tucalle.ui.usuario

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage

// Componentes y ViewModels
import com.tech.tucalle.ui.components.*
import com.tech.tucalle.ui.viewmodel.AuthViewModel
import com.tech.tucalle.ui.viewmodel.HomeViewModel
import com.tech.tucalle.navigation.BottomNavigationBarDynamic

@Composable
fun HomeScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel = viewModel(),
    homeViewModel: HomeViewModel = viewModel(),
    rol: String = "USUARIO", // 🔴 Recibe el rol dinámico (Puede ser QUALITY)
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
        // 🔴 Utilizamos la barra de navegación Global y pasamos el NavController
        bottomBar = {
            BottomNavigationBarDynamic(
                rol = rol,
                currentSelection = "Home",
                navController = navController
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    TopLocationBar(direccion = direccionReal)
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = { authViewModel.inyectarDatosDePrueba { } },
                        modifier = Modifier.fillMaxWidth().height(40.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
                    ) {
                        Text("Cargar Huariques Reales", color = Color.White, fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.height(20.dp))

                    if (bannersReales.isNotEmpty()) {
                        PromoCarousel(banners = bannersReales.map { it.imageUrl })
                    } else {
                        Box(modifier = Modifier.fillMaxWidth().height(160.dp).clip(RoundedCornerShape(16.dp)).background(Color.LightGray))
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    CategoriesRow()
                    Spacer(modifier = Modifier.height(30.dp))
                }
            }

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
                                restaurante = "Huarique Real",
                                calificacion = plato.calificacionPlato.toString(),
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

            item {
                Column(modifier = Modifier.padding(start = 20.dp)) {
                    Text("Los más recomendados", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                    LazyRow {
                        items(tiendasReales.take(6)) { tienda ->
                            RecommendedLogo(imageUrl = tienda.portadaUrl)
                        }
                    }
                    Spacer(modifier = Modifier.height(30.dp))
                }
            }

            item {
                Column(modifier = Modifier.padding(start = 20.dp)) {
                    Text("Porque lo bueno se repite", fontSize = 20.sp, fontWeight = FontWeight.Bold)
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PromoCarousel(banners: List<String>) {
    val pagerState = rememberPagerState(pageCount = { banners.size })
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth().height(160.dp).clip(RoundedCornerShape(16.dp))
        ) { page ->
            AsyncImage(
                model = banners[page],
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.Center) {
            repeat(banners.size) { iteration ->
                val color = if (pagerState.currentPage == iteration) Color(0xFFD32F2F) else Color.LightGray
                Box(modifier = Modifier.padding(2.dp).clip(CircleShape).background(color).size(8.dp))
            }
        }
    }
}

@Composable
fun TopLocationBar(direccion: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFFD32F2F))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = direccion, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        }
        IconButton(onClick = { }) {
            Icon(Icons.Default.Notifications, contentDescription = null, tint = Color(0xFFD32F2F))
        }
    }
}

@Composable
fun CategoriesRow() {
    val categorias = listOf("Broaster", "Caldos", "Parrilla", "Ensaladas")
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        categorias.forEach { cat ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(modifier = Modifier.size(60.dp).clip(CircleShape).background(Color(0xFFFFF0F0)), contentAlignment = Alignment.Center) {
                    Icon(Icons.Outlined.ShoppingCart, contentDescription = null, tint = Color(0xFFD32F2F))
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = cat, fontSize = 12.sp, color = Color.DarkGray)
            }
        }
    }
}

@Composable
fun SearchBarUI() {
    var query by remember { mutableStateOf("") }
    OutlinedTextField(
        value = query, onValueChange = { query = it }, placeholder = { Text("Buscar huariques...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Color(0xFFE0E0E0), focusedBorderColor = Color(0xFFD32F2F))
    )
}