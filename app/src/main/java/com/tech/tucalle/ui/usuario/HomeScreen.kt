package com.tech.tucalle.ui.usuario

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
// ---  COMPONENTES IMPORTADOS ---
import com.tech.tucalle.ui.components.DishCard
import com.tech.tucalle.ui.components.RestaurantCard
import com.tech.tucalle.ui.components.SectionHeader
import com.tech.tucalle.ui.components.RecommendedLogo
import com.tech.tucalle.ui.components.RepeatCard
import com.tech.tucalle.ui.viewmodel.AuthViewModel

@Composable
fun HomeScreen(authViewModel: AuthViewModel = viewModel(),onRestaurantClick: (String) -> Unit = {}) {
    var statusMessage by remember { mutableStateOf("") }

    Scaffold(
        bottomBar = { BottomNavigationBar() },
        containerColor = Color.White
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // --- HEADER Y BUSCADOR ---
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    TopLocationBar(direccion = "Jr. Alcedo Beltrán 244")
                    Spacer(modifier = Modifier.height(10.dp))

                    if (statusMessage.isNotEmpty()) {
                        Text(
                            text = statusMessage,
                            color = if (statusMessage.contains("Error")) Color.Red else Color(0xFF4CAF50),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    Button(
                        onClick = {
                            statusMessage = "Inyectando datos..."
                            authViewModel.inyectarDatosDePrueba { resultado ->
                                statusMessage = resultado
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                    ) {
                        Text("Inyectar Datos (Borrar luego)", fontSize = 16.sp, color = Color.White)
                    }
                    Spacer(modifier = Modifier.height(20.dp))

                    val bannersTemporales = listOf(
                        "https://images.unsplash.com/photo-1504674900247-0877df9cc836",
                        "https://images.unsplash.com/photo-1555939594-58d7cb561ad1",
                        "https://images.unsplash.com/photo-1493770348161-369560ae357d"
                    )
                    PromoCarousel(banners = bannersTemporales)
                    Spacer(modifier = Modifier.height(24.dp))

                    CategoriesRow()
                    Spacer(modifier = Modifier.height(24.dp))

                    Text("¿Qué se te antoja hoy?", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Spacer(modifier = Modifier.height(12.dp))
                    SearchBarUI()
                    Spacer(modifier = Modifier.height(30.dp))
                }
            }

            // --- SECCIÓN: POPULARES AHORA ---
            item {
                Column(modifier = Modifier.padding(start = 20.dp)) {
                    SectionHeader(title = "Populares ahora", onVerTodoClick = { /* Navegar a lista completa */ })
                    Spacer(modifier = Modifier.height(12.dp))

                    LazyRow {
                        item {
                            DishCard(
                                nombre = "Pizza carnivora",
                                restaurante = "Fast food",
                                calificacion = "4.8",
                                precioOriginal = "S/ 40.00",
                                precioDescuento = "S/ 30.00",
                                descuentoTag = "-25%",
                                imagenUrl = "https://images.unsplash.com/photo-1513104890138-7c749659a591"
                            )
                        }
                        item {
                            DishCard(
                                nombre = "Pollo a la Brasa",
                                restaurante = "Don Lucho",
                                calificacion = "4.8",
                                precioOriginal = "S/ 60.00",
                                precioDescuento = "S/ 45.00",
                                descuentoTag = "-25%",
                                imagenUrl = "https://images.unsplash.com/photo-1598514982205-f36b96d1e8d4"
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(30.dp))
                }
            }

            // --- SECCIÓN: RESTAURANTES CERCA A TI ---
// --- SECCIÓN: RESTAURANTES CERCA A TI ---
            item {
                Column(modifier = Modifier.padding(start = 20.dp)) {
                    SectionHeader(title = "Restaurantes cerca a ti", onVerTodoClick = { /* Navegar a lista completa */ })
                    Spacer(modifier = Modifier.height(12.dp))

                    LazyRow {
                        item {
                            RestaurantCard(
                                nombre = "Donde Luis - Broaster",
                                distrito = "Comas",
                                horario = "Cierra 9PM",
                                calificacion = "4.8",
                                etiquetas = listOf("Sandwich", "Broaster", "Pollo a la Brasa"),
                                portadaUrl = "https://images.unsplash.com/photo-1552566626-52f8b828add9",
                                onClick = { onRestaurantClick("mock_tienda_luis") } // <-- Clic para Donde Luis
                            )
                        }

                        // AQUÍ VA TU CÓDIGO NUEVO REEMPLAZANDO AL ANTERIOR
                        item {
                            RestaurantCard(
                                nombre = "Salchichones Flash",
                                distrito = "Villa El Salvador",
                                horario = "Cierra 3AM",
                                calificacion = "4.5",
                                etiquetas = listOf("Desayunos", "Almuerzos", "Cenas", "Jugos"),
                                portadaUrl = "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4",
                                onClick = { onRestaurantClick("mock_tienda_flash") } // <-- ¡El clic que redirige!
                            )
                        }

                        item {
                            RestaurantCard(
                                nombre = "Mayta",
                                distrito = "Miraflores",
                                horario = "Cierra 9PM",
                                calificacion = "4.8",
                                etiquetas = listOf("Almuerzos", "Gourmet"),
                                portadaUrl = "https://images.unsplash.com/photo-1514933651103-005eec06c04b",
                                onClick = { onRestaurantClick("mock_tienda_mayta") } // <-- Clic para Mayta
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(30.dp))
                }
            }
            // --- SECCIÓN: LOS MÁS RECOMENDADOS ---
            item {
                Column(modifier = Modifier.padding(start = 20.dp)) {
                    Text("Los más recomendados", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Spacer(modifier = Modifier.height(12.dp))

                    LazyRow {
                        val logosTemp = listOf(
                            "https://images.unsplash.com/photo-1599305445671-ac291c95aaa9",
                            "https://images.unsplash.com/photo-1599305445671-ac291c95aaa9",
                            "https://images.unsplash.com/photo-1599305445671-ac291c95aaa9",
                            "https://images.unsplash.com/photo-1599305445671-ac291c95aaa9"
                        )
                        items(logosTemp.size) { index ->
                            RecommendedLogo(imageUrl = logosTemp[index])
                        }
                    }
                    Spacer(modifier = Modifier.height(30.dp))
                }
            }

            // --- SECCIÓN: PORQUE LO BUENO SE REPITE ---
            item {
                Column(modifier = Modifier.padding(start = 20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(end = 20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Porque lo bueno se repite", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        Box(
                            modifier = Modifier
                                .background(Color.DarkGray, shape = RoundedCornerShape(12.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("Amigos", color = Color.White, fontSize = 10.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    LazyRow {
                        item {
                            RepeatCard(
                                imageUrl = "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4",
                                title = "Salchichones Flash"
                            )
                        }
                        item {
                            RepeatCard(
                                imageUrl = "https://images.unsplash.com/photo-1552566626-52f8b828add9",
                                title = "Donde Luis - Broaster"
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }
    }
}

// ------------------------------------------------------------------------
// COMPONENTES INTERNOS
// ------------------------------------------------------------------------

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PromoCarousel(banners: List<String>) {
    val pagerState = rememberPagerState(pageCount = { banners.size })

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .clip(RoundedCornerShape(16.dp))
        ) { page ->
            AsyncImage(
                model = banners[page],
                contentDescription = "Banner Promocional",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.wrapContentHeight(),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(banners.size) { iteration ->
                val color = if (pagerState.currentPage == iteration) Color(0xFFD32F2F) else Color.LightGray
                val size = if (pagerState.currentPage == iteration) 10.dp else 8.dp
                Box(
                    modifier = Modifier
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(size)
                )
            }
        }
    }
}

@Composable
fun TopLocationBar(direccion: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(top = 16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.LocationOn, contentDescription = "Ubicación", tint = Color(0xFFD32F2F))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = direccion, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        }
        IconButton(onClick = { /* Abrir notificaciones */ }) {
            Icon(Icons.Default.Notifications, contentDescription = "Notificaciones", tint = Color(0xFFD32F2F))
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
                    Icon(Icons.Outlined.ShoppingCart, contentDescription = cat, tint = Color(0xFFD32F2F))
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
        value = query, onValueChange = { query = it }, placeholder = { Text("Buscar huariques, platos...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar", tint = Color.Gray) },
        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Color(0xFFE0E0E0), focusedBorderColor = Color(0xFFD32F2F))
    )
}

@Composable
fun BottomNavigationBar() {
    NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
        val items = listOf("Mural", "Ofertas", "Pedidos", "Favoritos", "Perfil")
        val icons = listOf(Icons.Outlined.Search, Icons.Outlined.Star, Icons.Outlined.ShoppingCart, Icons.Outlined.FavoriteBorder, Icons.Outlined.Person)
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = { Icon(icons[index], contentDescription = item) }, label = { Text(item, fontSize = 10.sp) },
                selected = index == 0, onClick = { /* Navegar */ },
                colors = NavigationBarItemDefaults.colors(selectedIconColor = Color(0xFFD32F2F), selectedTextColor = Color(0xFFD32F2F), unselectedIconColor = Color.Gray, unselectedTextColor = Color.Gray, indicatorColor = Color.White)
            )
        }
    }
}