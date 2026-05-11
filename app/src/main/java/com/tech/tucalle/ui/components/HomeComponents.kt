package com.tech.tucalle.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@Composable
fun SectionHeader(title: String, onVerTodoClick: () -> Unit = {}) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        TextButton(onClick = onVerTodoClick, contentPadding = PaddingValues(0.dp)) {
            Text("Ver todo", color = Color(0xFFD32F2F), fontSize = 14.sp, textDecoration = TextDecoration.Underline)
        }
    }
}

@Composable
fun DishCard(
    nombre: String,
    restaurante: String,
    calificacion: String,
    precioOriginal: String,
    precioDescuento: String,
    descuentoTag: String,
    imagenUrl: String
) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .padding(end = 12.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().height(140.dp)) {
                AsyncImage(
                    model = imagenUrl,
                    contentDescription = nombre,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Surface(
                    shape = CircleShape,
                    color = Color.White,
                    modifier = Modifier.padding(8.dp).size(32.dp).align(Alignment.TopEnd)
                ) {
                    Icon(imageVector = Icons.Default.FavoriteBorder, contentDescription = "Favorito", tint = Color.Gray, modifier = Modifier.padding(6.dp))
                }
            }

            Column(modifier = Modifier.padding(12.dp)) {
                Text(text = nombre, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black, maxLines = 1, overflow = TextOverflow.Ellipsis)

                Row(modifier = Modifier.fillMaxWidth().padding(top = 4.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(text = restaurante, fontSize = 12.sp, color = Color.Gray)
                    Text(text = calificacion, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                }

                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)) {
                    Box(modifier = Modifier.background(Color(0xFFFFD54F), shape = RoundedCornerShape(4.dp)).padding(horizontal = 4.dp, vertical = 2.dp)) {
                        Text(text = descuentoTag, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = precioOriginal, fontSize = 10.sp, color = Color.Gray, textDecoration = TextDecoration.LineThrough)
                }

                Text(text = precioDescuento, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD32F2F), modifier = Modifier.padding(top = 4.dp))
            }
        }
    }
}


@OptIn(ExperimentalLayoutApi::class) // Para usar FlowRow
@Composable
fun RestaurantCard(
    nombre: String,
    distrito: String,
    horario: String,
    calificacion: String,
    etiquetas: List<String>,
    portadaUrl: String,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .width(280.dp) // Un poco más ancho para dar aire al texto
            .padding(end = 16.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Imagen con altura fija proporcional
            Box(modifier = Modifier.fillMaxWidth().height(140.dp)) {
                AsyncImage(
                    model = portadaUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Column(modifier = Modifier.padding(12.dp)) {
                // Nombre y Rating en una sola línea controlada
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = nombre,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis, // Evita que el nombre rompa la fila
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = calificacion,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.DarkGray
                    )
                }

                // Distrito y Horario con Ellipsis para que no se corten feo
                Text(
                    text = "$distrito • $horario",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // SOLUCIÓN RESPONSIVE: FlowRow para las etiquetas
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    etiquetas.forEach { etiqueta ->
                        Box(
                            modifier = Modifier
                                .background(Color(0xFFD32F2F), shape = RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = etiqueta,
                                fontSize = 10.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
// ---------------------------------------------------------
// LOGO CIRCULAR (Para "Los más recomendados")
// ---------------------------------------------------------
@Composable
fun RecommendedLogo(imageUrl: String, onClick: () -> Unit = {}) {
    Box(
        modifier = Modifier
            .padding(end = 12.dp)
            .size(70.dp)
            .clip(CircleShape)
            .background(Color.LightGray)
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = "Logo Recomendado",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}

// ---------------------------------------------------------
// TARJETA GRANDE (Para "Porque lo bueno se repite")
// ---------------------------------------------------------
@Composable
fun RepeatCard(imageUrl: String, title: String, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier
            .width(280.dp)
            .height(200.dp)
            .padding(end = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            AsyncImage(
                model = imageUrl,
                contentDescription = title,
                modifier = Modifier.fillMaxWidth().weight(0.6f),
                contentScale = ContentScale.Crop
            )
            Box(modifier = Modifier.fillMaxWidth().weight(0.4f).padding(12.dp)) {
                Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            }
        }
    }
}