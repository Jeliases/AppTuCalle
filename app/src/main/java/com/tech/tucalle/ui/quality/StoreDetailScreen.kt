package com.tech.tucalle.ui.usuario

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.LocationOn
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.tech.tucalle.domain.model.ComentarioTienda
import com.tech.tucalle.domain.model.Plato
import com.tech.tucalle.navigation.BottomNavigationBarDynamic
import com.tech.tucalle.ui.theme.Poppins
import com.tech.tucalle.ui.theme.Roboto
import com.tech.tucalle.ui.viewmodel.ComentariosViewModel
import com.tech.tucalle.ui.viewmodel.StoreDetailViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun StoreDetailScreen(
    navController: NavHostController,
    rol: String = "USUARIO",
    idTienda: String,
    viewModel: StoreDetailViewModel = viewModel(),
    comentariosViewModel: ComentariosViewModel = viewModel()
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val colorRojo = Color(0xFFD32F2F)
    val tienda by viewModel.tienda.collectAsState()
    val platos by viewModel.platos.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val comentarios by comentariosViewModel.comentarios.collectAsState()
    val currentUid = comentariosViewModel.obtenerUidActual()

    var comentarioText by remember { mutableStateOf("") }
    var isSending by remember { mutableStateOf(false) }
    var filtroSeleccionado by remember { mutableStateOf("Destacados") }

    LaunchedEffect(idTienda) {
        viewModel.cargarDatosTienda(idTienda)
        comentariosViewModel.cargarComentarios(idTienda)
    }

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
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = colorRojo)
            }
            return@Scaffold
        }

        val store = tienda ?: return@Scaffold

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
                        Icon(Icons.Outlined.LocationOn, contentDescription = "Ubicación", tint = colorRojo, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = store.direccion.texto.split(",").firstOrNull() ?: "Ubicación",
                            fontFamily = Roboto,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )
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
                        model = store.portadaUrl.ifBlank { "https://via.placeholder.com/800x400.png?text=Sin+Portada" },
                        contentDescription = "Portada",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    if (rol == "QUALITY") {
                        val isGuardado by viewModel.isGuardado.collectAsState()
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(12.dp)
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                                .clickable {
                                    viewModel.toggleGuardarTienda(store.uid)
                                    // 🔥 LANZAMOS EL MENSAJE EMERGENTE AL GUARDAR
                                    if (!isGuardado) {
                                        android.widget.Toast.makeText(context, "Huarique guardado para su reseña", android.widget.Toast.LENGTH_SHORT).show()
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isGuardado) Icons.Filled.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "Guardar",
                                tint = if (isGuardado) colorRojo else Color.Gray,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

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
            // 3. INFORMACIÓN PRINCIPAL (DISEÑO EXACTO FIGMA)
            // ==========================================
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        // Lado Izquierdo: Título y Ubicación/Horario
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = store.nombre, fontFamily = Roboto, fontWeight = FontWeight.Bold, fontSize = 28.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Outlined.LocationOn, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(store.direccion.texto.split(",").lastOrNull()?.trim() ?: "Perú", fontFamily = Poppins, color = Color.Gray, fontSize = 13.sp)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Outlined.AccessTime, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Cierra ${store.horarioCierre.ifBlank { "N/A" }}", fontFamily = Poppins, color = Color.Gray, fontSize = 13.sp)
                            }
                        }

                        // Lado Derecho: Rating y Botón Abierto
                        Column(horizontalAlignment = Alignment.End) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Star, contentDescription = "Rating", tint = Color(0xFFFFC107), modifier = Modifier.size(28.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(String.format("%.1f", store.calificacionGeneral), fontFamily = Roboto, fontWeight = FontWeight.Bold, fontSize = 26.sp, color = Color.Gray)
                                Text(" (${store.totalResenas})", fontFamily = Poppins, fontSize = 12.sp, color = Color.Gray)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (store.estadoLocal.equals("Abierto", ignoreCase = true)) colorRojo else Color.Gray)
                                    .padding(horizontal = 16.dp, vertical = 6.dp)
                            ) {
                                Text(store.estadoLocal.uppercase(), fontFamily = Roboto, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Etiquetas (Pills Rojos)
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        store.etiquetas.forEach { tag ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(colorRojo)
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(tag, fontFamily = Roboto, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 11.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // BOTONES DE CONTACTO (RESTURADOS)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(
                            onClick = { /* Acción WhatsApp */ },
                            modifier = Modifier.weight(1f).height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = colorRojo),
                            shape = RoundedCornerShape(25.dp)
                        ) {
                            Icon(Icons.Default.Call, contentDescription = "WhatsApp", modifier = Modifier.size(20.dp)) // Usamos Call como placeholder de WP
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("WhatsApp", fontFamily = Roboto, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                        Button(
                            onClick = { /* Acción Llamar */ },
                            modifier = Modifier.weight(1f).height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = colorRojo),
                            shape = RoundedCornerShape(25.dp)
                        ) {
                            Icon(Icons.Default.Phone, contentDescription = "Llamar", modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Llamar", fontFamily = Roboto, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    }
                }
            }

            // ==========================================
            // 4. CHIPS DE FILTRO (DISEÑO EXACTO FIGMA)
            // ==========================================
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Destacados (Con ícono, sin fondo)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { filtroSeleccionado = "Destacados" }
                    ) {
                        Icon(Icons.Default.Menu, contentDescription = "Destacados", tint = if (filtroSeleccionado == "Destacados") colorRojo else Color.Black, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Destacados", fontFamily = Poppins, fontSize = 14.sp, color = if (filtroSeleccionado == "Destacados") colorRojo else Color.Black)
                    }

                    // Ofertas (Píldora)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (filtroSeleccionado == "Ofertas") colorRojo else Color(0xFFE0E0E0))
                            .clickable { filtroSeleccionado = "Ofertas" }
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Text("Ofertas", fontFamily = Poppins, fontSize = 14.sp, color = if (filtroSeleccionado == "Ofertas") Color.White else Color.Black)
                    }

                    // Favoritos (Píldora)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (filtroSeleccionado == "Favoritos") colorRojo else Color(0xFFE0E0E0))
                            .clickable { filtroSeleccionado = "Favoritos" }
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Text("Favoritos", fontFamily = Poppins, fontSize = 14.sp, color = if (filtroSeleccionado == "Favoritos") Color.White else Color.Black)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // ==========================================
            // 5. LISTA DE PLATOS
            // ==========================================
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    val platosFiltrados = when (filtroSeleccionado) {
                        "Ofertas" -> platos.filter { it.precioDescuento < it.precioOriginal }
                        else -> platos // "Destacados" o "Favoritos"
                    }
                    if (platosFiltrados.isEmpty()) {
                        Text("No hay platos para mostrar.", color = Color.Gray, fontFamily = Poppins, fontSize = 13.sp, modifier = Modifier.padding(vertical = 20.dp))
                    } else {
                        platosFiltrados.forEach { plato -> DishListItem(plato = plato) }
                    }
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
                HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 1.dp, modifier = Modifier.padding(vertical = 10.dp))
            }

            // ==========================================
            // 7. CAJA DE COMENTARIOS (FUNCIONAL)
            // ==========================================
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp).padding(top = 10.dp)) {
                    Text("Comentarios", fontFamily = Roboto, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                    Text("Escribe tu comentario", fontFamily = Poppins, fontSize = 14.sp, color = Color.Gray)

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = comentarioText,
                        onValueChange = { comentarioText = it },
                        modifier = Modifier.fillMaxWidth().height(55.dp),
                        placeholder = { Text("Escribe algo increíble...", fontFamily = Poppins) },
                        trailingIcon = {
                            if (isSending) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = colorRojo, strokeWidth = 2.dp)
                            } else {
                                IconButton(onClick = {
                                    if (comentarioText.isNotBlank()) {
                                        isSending = true
                                        comentariosViewModel.enviarComentario(store.uid, store.nombre, comentarioText)
                                        comentarioText = ""
                                        isSending = false
                                    }

                                }) {
                                    Icon(Icons.Outlined.Send, contentDescription = "Enviar", tint = if (comentarioText.isNotBlank()) colorRojo else Color.Gray)
                                }
                            }
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color.LightGray,
                            focusedBorderColor = colorRojo
                        )
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            // ==========================================
            // 8. LISTA DE COMENTARIOS REALES TIPO TIKTOK
            // ==========================================
            if (comentarios.isEmpty()) {
                item {
                    Text("Sé el primero en dejar un comentario.", color = Color.Gray, fontFamily = Poppins, fontSize = 13.sp, modifier = Modifier.padding(horizontal = 20.dp))
                }
            } else {
                items(comentarios) { comentario ->
                    Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                        if (comentario.rolUsuario == "QUALITY") {
                            QualityReviewItem(comentario, currentUid) { id -> comentariosViewModel.toggleLike(id) }
                        } else {
                            NormalReviewItem(comentario, currentUid) { id -> comentariosViewModel.toggleLike(id) }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(100.dp)) }
        }
    }
}

// ==========================================
// COMPONENTES REUTILIZABLES
// ==========================================

// 🔥 DISEÑO EXACTO PARA EL COMENTARIO DE QUALITY
@Composable
private fun QualityReviewItem(comentario: ComentarioTienda, currentUid: String, onLikeClick: (String) -> Unit) {
    val dateFormatter = SimpleDateFormat("d MMM yyyy", Locale("es", "ES"))
    val isLiked = comentario.likedBy.contains(currentUid)

    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
            Row {
                AsyncImage(model = comentario.fotoUsuario.ifBlank { "https://ui-avatars.com/api/?name=${comentario.nombreUsuario}&background=D32F2F&color=fff" }, contentDescription = "Avatar", modifier = Modifier.size(44.dp).clip(CircleShape).background(Color.LightGray), contentScale = ContentScale.Crop)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = comentario.nombreUsuario, fontFamily = Roboto, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Quality", fontFamily = Poppins, fontSize = 11.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "🎁 2025 🏅", fontSize = 10.sp)
                    }
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("estuvo en ", fontFamily = Poppins, fontSize = 11.sp, color = Color.Gray)
                Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFFD32F2F), modifier = Modifier.size(14.dp))
                Text(comentario.nombreTienda, fontFamily = Roboto, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text(text = comentario.texto, fontFamily = Poppins, fontSize = 13.sp, lineHeight = 18.sp, color = Color(0xFF333333))

        // 🔥 TEXTO EN NEGRITA EXACTAMENTE COMO EL FIGMA
        if (comentario.platosSugeridos.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("Plato sugerido: ", fontFamily = Roboto, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.Black)
                Text(
                    text = comentario.platosSugeridos.joinToString(", "),
                    fontFamily = Roboto,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color.Black
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(String.format("%.1f", comentario.calificacion), fontFamily = Roboto, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.Gray)
                Spacer(modifier = Modifier.width(8.dp))
                Text(dateFormatter.format(Date(comentario.fecha)), fontFamily = Poppins, fontSize = 11.sp, color = Color.Gray)
            }
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { onLikeClick(comentario.id) }.padding(4.dp)) {
                Text("${comentario.likes}", fontFamily = Poppins, fontSize = 13.sp, color = if (isLiked) Color(0xFFD32F2F) else Color.Gray)
                Spacer(modifier = Modifier.width(4.dp))
                Icon(imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder, contentDescription = "Like", tint = if (isLiked) Color(0xFFD32F2F) else Color.Gray, modifier = Modifier.size(22.dp))
            }
        }
        HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 1.dp, modifier = Modifier.padding(top = 16.dp))
    }
}
@Composable
private fun NormalReviewItem(comentario: ComentarioTienda, currentUid: String, onLikeClick: (String) -> Unit) {
    val dateFormatter = SimpleDateFormat("d MMM yyyy", Locale("es", "ES"))
    val isLiked = comentario.likedBy.contains(currentUid)

    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
            Row {
                AsyncImage(model = comentario.fotoUsuario.ifBlank { "https://ui-avatars.com/api/?name=${comentario.nombreUsuario}&background=D32F2F&color=fff" }, contentDescription = "Avatar", modifier = Modifier.size(44.dp).clip(CircleShape).background(Color.LightGray), contentScale = ContentScale.Crop)
                Spacer(modifier = Modifier.width(12.dp))
                Column { Text(text = comentario.nombreUsuario, fontFamily = Roboto, fontWeight = FontWeight.Bold, fontSize = 15.sp) }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("estuvo en ", fontFamily = Poppins, fontSize = 11.sp, color = Color.Gray)
                Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFFD32F2F), modifier = Modifier.size(14.dp))
                Text(comentario.nombreTienda, fontFamily = Roboto, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = comentario.texto, fontFamily = Poppins, fontSize = 13.sp, lineHeight = 18.sp, color = Color(0xFF333333))
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(dateFormatter.format(Date(comentario.fecha)), fontFamily = Poppins, fontSize = 11.sp, color = Color.Gray)
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { onLikeClick(comentario.id) }.padding(4.dp)) {
                Text("${comentario.likes}", fontFamily = Poppins, fontSize = 13.sp, color = if (isLiked) Color(0xFFD32F2F) else Color.Gray)
                Spacer(modifier = Modifier.width(4.dp))
                Icon(imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder, contentDescription = "Like", tint = if (isLiked) Color(0xFFD32F2F) else Color.Gray, modifier = Modifier.size(22.dp))
            }
        }
        HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 1.dp, modifier = Modifier.padding(top = 16.dp))
    }
}

@Composable
private fun DishListItem(plato: Plato) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp).background(Color.White), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(110.dp).clip(RoundedCornerShape(16.dp))) {
            AsyncImage(model = plato.imagenUrl, contentDescription = plato.nombre, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
            Box(modifier = Modifier.align(Alignment.TopEnd).padding(8.dp).size(28.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.9f)), contentAlignment = Alignment.Center) {
                Icon(Icons.Outlined.FavoriteBorder, contentDescription = "Favorito", tint = Color.Gray, modifier = Modifier.size(16.dp))
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = plato.nombre, fontFamily = Roboto, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = plato.descripcion, fontFamily = Poppins, fontSize = 11.sp, color = Color.Gray, maxLines = 2, overflow = TextOverflow.Ellipsis, lineHeight = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            if (plato.precioDescuento < plato.precioOriginal) {
                val porcentaje = ((1 - (plato.precioDescuento / plato.precioOriginal)) * 100).toInt()
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(Color(0xFFFFD54F)).padding(horizontal = 4.dp, vertical = 2.dp)) {
                        Text(text = "-$porcentaje%", fontFamily = Roboto, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "S/ ${String.format("%.2f", plato.precioOriginal)}", fontFamily = Poppins, fontSize = 10.sp, color = Color.Gray, textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough)
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = "S/ ${String.format("%.2f", plato.precioDescuento)}", fontFamily = Roboto, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            } else {
                Text(text = "S/ ${String.format("%.2f", plato.precioOriginal)}", fontFamily = Roboto, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FlowRow(modifier: Modifier = Modifier, horizontalArrangement: Arrangement.Horizontal = Arrangement.Start, verticalArrangement: Arrangement.Vertical = Arrangement.Top, content: @Composable () -> Unit) {
    androidx.compose.foundation.layout.FlowRow(modifier = modifier, horizontalArrangement = horizontalArrangement, verticalArrangement = verticalArrangement) { content() }
}