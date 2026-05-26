package com.tech.tucalle.ui.usuario

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.tech.tucalle.navigation.BottomNavigationBarDynamic
import com.tech.tucalle.ui.theme.Poppins
import com.tech.tucalle.ui.theme.Roboto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreDetailScreen(
    navController: NavHostController,
    rol: String = "USUARIO", // 🔥 Recibe el rol dinámico
    storeName: String = "Mayta", // Estos datos luego vendrán de tu ViewModel
    portadaUrl: String = "https://via.placeholder.com/800x400.png?text=Portada+Huarique",
    calificacion: Double = 4.8,
    totalResenas: Int = 15
) {
    var comentarioText by remember { mutableStateOf("") }
    val colorRojo = Color(0xFFD32F2F)

    Scaffold(
        containerColor = Color.White,
        bottomBar = {
            BottomNavigationBarDynamic(
                rol = rol,
                currentSelection = "Home",
                navController = navController
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // ==========================================
            // 1. TOP BAR (Dirección)
            // ==========================================
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.Black)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Icon(Icons.Default.LocationOn, contentDescription = "Ubicación", tint = colorRojo, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Jr. Alcedo Beltrán 244",
                            fontFamily = Roboto,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Desplegar", tint = Color.Black)
                    }
                    Icon(Icons.Default.NotificationsNone, contentDescription = "Notificaciones", tint = colorRojo)
                }
            }

            // ==========================================
            // 2. PORTADA Y BOTONES SUPERPUESTOS
            // ==========================================
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .padding(horizontal = 20.dp)
                        .clip(RoundedCornerShape(16.dp))
                ) {
                    AsyncImage(
                        model = portadaUrl,
                        contentDescription = "Portada",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    // 🔥 BOTÓN GUARDAR: SOLO VISIBLE PARA QUALITY
                    if (rol == "QUALITY") {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(12.dp)
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.BookmarkBorder, contentDescription = "Guardar", tint = Color.Gray, modifier = Modifier.size(20.dp))
                        }
                    }

                    // Botón Favorito (Corazón)
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(12.dp)
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Outlined.FavoriteBorder, contentDescription = "Favorito", tint = Color.Gray, modifier = Modifier.size(20.dp))
                    }
                }
            }

            // ==========================================
            // 3. INFORMACIÓN PRINCIPAL (Título, Rating, Etiquetas)
            // ==========================================
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column {
                            Text(text = storeName, fontFamily = Roboto, fontWeight = FontWeight.Bold, fontSize = 28.sp)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(12.dp))
                                Text(" Comas", fontFamily = Poppins, color = Color.Gray, fontSize = 12.sp)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AccessTime, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(12.dp))
                                Text(" Cierra 9PM", fontFamily = Poppins, color = Color.Gray, fontSize = 12.sp)
                            }
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Star, contentDescription = "Rating", tint = Color(0xFFFFC107), modifier = Modifier.size(28.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("$calificacion", fontFamily = Roboto, fontWeight = FontWeight.Bold, fontSize = 24.sp, color = Color.Gray)
                                Text("($totalResenas)", fontFamily = Poppins, fontSize = 12.sp, color = Color.Gray)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(colorRojo)
                                    .padding(horizontal = 12.dp, vertical = 4.dp)
                            ) {
                                Text("ABIERTO", fontFamily = Roboto, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 10.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Etiquetas (Pills)
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("Hamburguesas", "Broaster", "Shawarmas", "Piqueos", "Salchipapas").forEach { tag ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(colorRojo)
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(tag, fontFamily = Roboto, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 10.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Botones de Contacto
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(
                            onClick = { /* Acción WhatsApp */ },
                            modifier = Modifier.weight(1f).height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = colorRojo),
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            Icon(Icons.Default.Chat, contentDescription = "WhatsApp", modifier = Modifier.size(18.dp)) // Idealmente usar icono de WP
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("WhatsApp", fontFamily = Roboto, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Button(
                            onClick = { /* Acción Llamar */ },
                            modifier = Modifier.weight(1f).height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = colorRojo),
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            Icon(Icons.Default.Phone, contentDescription = "Llamar", modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Llamar", fontFamily = Roboto, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }

            // ==========================================
            // 4. CHIPS DE FILTRO (Destacados, Ofertas...)
            // ==========================================
            item {
                LazyRow(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = true,
                            onClick = { },
                            label = { Text("Destacados", fontFamily = Poppins, fontSize = 12.sp) },
                            leadingIcon = { Icon(Icons.Default.Menu, contentDescription = null, modifier = Modifier.size(16.dp)) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color.Transparent)
                        )
                    }
                    item {
                        FilterChip(
                            selected = true,
                            onClick = { },
                            label = { Text("Ofertas", fontFamily = Poppins, fontSize = 12.sp, color = Color.White) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = colorRojo)
                        )
                    }
                    item {
                        FilterChip(
                            selected = false,
                            onClick = { },
                            label = { Text("Favoritos", fontFamily = Poppins, fontSize = 12.sp) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // ==========================================
            // 5. LISTA DE PLATOS
            // ==========================================
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    DishListItem(
                        nombre = "Hamburguesa de pollo",
                        descripcion = "Hamburguesa de filete de pechuga, ala o entrepierna mas papas y ensalada con t...",
                        precio = "17.00",
                        precioAntiguo = "S/ 40.00",
                        descuento = "-25%",
                        imagen = "https://via.placeholder.com/150"
                    )
                    DishListItem(
                        nombre = "Hamburguesa de carne",
                        descripcion = "Hamburguesa de filete de pechuga, ala o entrepierna mas papas y ensalada con t...",
                        precio = "17.00",
                        imagen = "https://via.placeholder.com/150"
                    )
                    DishListItem(
                        nombre = "Salchipapa",
                        descripcion = "Hamburguesa de filete de pechuga, ala o entrepierna mas papas y ensalada con t...",
                        precio = "17.00",
                        imagen = "https://via.placeholder.com/150"
                    )
                }
            }

            // ==========================================
            // 6. BOTÓN MENÚ COMPLETO
            // ==========================================
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 20.dp)) {
                    Button(
                        onClick = { },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = colorRojo),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Revisa el menú completo", fontFamily = Roboto, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
                Divider(color = Color(0xFFEEEEEE), thickness = 1.dp, modifier = Modifier.padding(vertical = 10.dp))
            }

            // ==========================================
            // 7. SECCIÓN DE COMENTARIOS Y RESEÑAS
            // ==========================================
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Text("Comentarios", fontFamily = Roboto, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                    Text("Escribe tu comentario", fontFamily = Poppins, fontSize = 14.sp, color = Color.Gray)

                    Spacer(modifier = Modifier.height(10.dp))

                    // Input de comentario
                    OutlinedTextField(
                        value = comentarioText,
                        onValueChange = { comentarioText = it },
                        modifier = Modifier.fillMaxWidth().height(55.dp),
                        placeholder = { Text("...", fontFamily = Poppins) },
                        trailingIcon = {
                            IconButton(onClick = { /* Enviar comentario */ }) {
                                Icon(Icons.Outlined.Send, contentDescription = "Enviar", tint = Color.Gray)
                            }
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color.LightGray,
                            focusedBorderColor = colorRojo
                        )
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Reseñas Reales
                    ReviewItem(
                        nombre = "Sofia Perez",
                        rol = "Quality",
                        fecha = "24 nov 2025",
                        rating = 4.8,
                        comentario = "Fui a comer a Don Lucho y la verdad, la atención es agradable, rápida y muy higiénica, 1000% recomendado cumple con CHAS.",
                        platoSugerido = "Hamburguesa",
                        likes = 35
                    )

                    ReviewItem(
                        nombre = "Pepe Salvado",
                        rol = "",
                        fecha = "5 dic 2025",
                        rating = 0.0, // Sin rating en el diseño para usuarios normales
                        comentario = "Buena atención, me dieron una bebida gratis por mi cumpleaños",
                        likes = 10
                    )

                    Spacer(modifier = Modifier.height(100.dp)) // Espacio para el BottomBar
                }
            }
        }
    }
}

// ==========================================
// COMPONENTES PRIVADOS DE LA PANTALLA
// ==========================================

@Composable
private fun DishListItem(
    nombre: String,
    descripcion: String,
    precio: String,
    precioAntiguo: String? = null,
    descuento: String? = null,
    imagen: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .background(Color.White),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Imagen del plato con botón corazón
        Box(
            modifier = Modifier
                .size(110.dp)
                .clip(RoundedCornerShape(16.dp))
        ) {
            AsyncImage(
                model = imagen,
                contentDescription = nombre,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.9f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.FavoriteBorder, contentDescription = "Favorito", tint = Color.Gray, modifier = Modifier.size(16.dp))
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Info del plato
        Column(modifier = Modifier.weight(1f)) {
            Text(text = nombre, fontFamily = Roboto, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = descripcion,
                fontFamily = Poppins,
                fontSize = 11.sp,
                color = Color.Gray,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 14.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (descuento != null && precioAntiguo != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(Color(0xFFFFD54F)).padding(horizontal = 4.dp, vertical = 2.dp)) {
                        Text(text = descuento, fontFamily = Roboto, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = precioAntiguo, fontFamily = Poppins, fontSize = 10.sp, color = Color.Gray, textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough)
                }
                Spacer(modifier = Modifier.height(2.dp))
            }

            Text(text = "S/ $precio", fontFamily = Roboto, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }
    }
}

@Composable
private fun ReviewItem(
    nombre: String,
    rol: String,
    fecha: String,
    rating: Double,
    comentario: String,
    platoSugerido: String? = null,
    likes: Int
) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = "https://ui-avatars.com/api/?name=$nombre&background=random",
                    contentDescription = "Avatar",
                    modifier = Modifier.size(44.dp).clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = nombre, fontFamily = Roboto, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    if (rol.isNotEmpty()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = rol, fontFamily = Poppins, fontSize = 11.sp, color = Color.Gray)
                            // Aquí irían los iconos de medallas (🎁, etc)
                        }
                    }
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("estuvo en ", fontFamily = Poppins, fontSize = 11.sp, color = Color.Gray)
                Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFFD32F2F), modifier = Modifier.size(14.dp))
                Text("Mayta", fontFamily = Roboto, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = comentario,
            fontFamily = Poppins,
            fontSize = 13.sp,
            lineHeight = 18.sp,
            color = Color(0xFF333333)
        )

        if (platoSugerido != null) {
            Spacer(modifier = Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Plato sugerido: ", fontFamily = Roboto, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text(platoSugerido, fontFamily = Roboto, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(Color(0xFFD32F2F)).padding(horizontal = 8.dp, vertical = 2.dp)) {
                Text("Hamburguesa", fontFamily = Roboto, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 9.sp)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (rating > 0.0) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("$rating", fontFamily = Roboto, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(fecha, fontFamily = Poppins, fontSize = 11.sp, color = Color.Gray)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("$likes", fontFamily = Poppins, fontSize = 12.sp, color = Color.Gray)
                Spacer(modifier = Modifier.width(4.dp))
                Icon(Icons.Default.ChangeHistory, contentDescription = "Útil", tint = Color.Gray, modifier = Modifier.size(16.dp))
            }
        }

        Divider(color = Color(0xFFEEEEEE), thickness = 1.dp, modifier = Modifier.padding(top = 16.dp))
    }
}

// FlowRow nativo simplificado (para que compile en Compose Material 3 sin librerías extra)
@Composable
private fun FlowRow(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    content: @Composable () -> Unit
) {
    // Para simplificar y asegurar que compile directo, usamos un envoltorio nativo
    @OptIn(ExperimentalLayoutApi::class)
    androidx.compose.foundation.layout.FlowRow(
        modifier = modifier,
        horizontalArrangement = horizontalArrangement,
        verticalArrangement = verticalArrangement
    ) {
        content()
    }
}