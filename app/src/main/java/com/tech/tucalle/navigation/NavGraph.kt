package com.tech.tucalle.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

// Importaciones de tus pantallas reales
import com.tech.tucalle.ui.auth.*
import com.tech.tucalle.ui.usuario.HomeScreen
import com.tech.tucalle.ui.huarique.StoreHomeScreen
import com.tech.tucalle.ui.viewmodel.AuthViewModel

@Composable
fun NavGraph(navController: NavHostController) {
    val authViewModel: AuthViewModel = viewModel()

    NavHost(navController = navController, startDestination = "splash") {

        composable("splash") {
            SplashScreen(onNavigateToLogin = {
                navController.navigate("auth") {
                    popUpTo("splash") { inclusive = true }
                }
            })
        }

        composable("auth") {
            AuthScreen(
                onNavigateToLogin = { navController.navigate("login") },
                onNavigateToGoogle = {
                    authViewModel.checkAndCreateGoogleUser { rol ->
                        if (rol == "USUARIO") {
                            navController.navigate("home_user") {
                                popUpTo("auth") { inclusive = true }
                            }
                        }
                    }
                }
            )
        }

        composable("login") {
            LoginScreen(
                onNavigateToRegister = { navController.navigate("user_type") },
                onBack = { navController.popBackStack() },
                // REDIRECCIÓN COMPLETA POR ROLES SEGÚN TU DIAGRAMA
                onLoginSuccess = { rol ->
                    val destino = when (rol) {
                        "TIENDA" -> "home_tienda"
                        "QUALITY" -> "home_quality"
                        "ADMIN" -> "home_admin"
                        else -> "home_user"
                    }
                    navController.navigate(destino) {
                        popUpTo("auth") { inclusive = true }
                    }
                },
                onNavigateToForgot = {
                    navController.navigate("forgot_password")
                }
            )
        }

        composable("forgot_password") {
            ForgotPasswordScreen(
                onBack = { navController.popBackStack() },
                onNavigateToVerify = { email ->
                    navController.navigate("verify_code/$email")
                }
            )
        }

        composable(
            route = "verify_code/{email}",
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            VerifyCodeScreen(
                email = email,
                onBack = {
                    navController.navigate("login") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        // --- FLUJO DE REGISTRO ---
        composable("user_type") {
            UserTypeScreen(
                onTypeSelected = { tipo ->
                    if (tipo == "TIENDA") {
                        navController.navigate("register_store")
                    } else {
                        navController.navigate("register/$tipo")
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable("register/{tipo}") { backStackEntry ->
            val tipo = backStackEntry.arguments?.getString("tipo") ?: "USUARIO"
            RegisterScreen(
                tipo = tipo,
                onBack = { navController.popBackStack() },
                onRegisterSuccess = { // <-- LE QUITAMOS EL "rol ->"
                    // Usamos la variable "tipo" para saber a dónde mandarlo
                    val destino = if (tipo == "QUALITY") "home_quality" else "home_user"
                    navController.navigate(destino) {
                        popUpTo("auth") { inclusive = true }
                    }
                }
            )
        }

        composable("register_store") {
            RegisterStoreScreen(
                onBack = { navController.popBackStack() },
                onNavigateToStoreDashboard = {
                    navController.navigate("home_tienda") {
                        popUpTo("auth") { inclusive = true }
                    }
                }
            )
        }

        // ==========================================================
        // ARQUITECTURA DE HOMES POR PERFIL (Fase 1 Estructural)
        // ==========================================================

        // A. HOME USUARIO (Mural principal modificado)
        composable("home_user") {
            HomeScreen(
                authViewModel = authViewModel,
                onRestaurantClick = { idTienda ->
                    navController.navigate("mural_tienda/$idTienda")
                }
            )
        }

        // B. HOME QUALITY (Reseñas, Huariques y Perfil)
        composable("home_quality") {
            PlaceholderHome(
                rol = "QUALITY",
                titulo = "Home Perfil Quality",
                subtitulo = "Aquí podrás evaluar locales con el método CHAS y recomendar platos de forma dinámica."
            )
        }

        // C. HOME HUARIQUE / TIENDA (Ajustes de local, Platos y Métricas)
        composable("home_tienda") {
            // Integra tu StoreHomeScreen real aquí
            StoreHomeScreen(authViewModel = authViewModel)
        }

        // D. HOME ADMINISTRADOR / TI (Sala de espera de Platos y Moderación)
        composable("home_admin") {
            PlaceholderHome(
                rol = "ADMIN",
                titulo = "Panel Administrativo / TI",
                subtitulo = "Sala de aprobaciones pendientes para nuevos platos y validación de comercios."
            )
        }

        // CONVERGENCIA: MURAL INDEPENDIENTE DESDE CARDS
        composable(
            route = "mural_tienda/{idTienda}",
            arguments = listOf(navArgument("idTienda") { type = NavType.StringType })
        ) { backStackEntry ->
            val idTienda = backStackEntry.arguments?.getString("idTienda") ?: ""
            MuralTiendaBlancoScreen(idTienda = idTienda)
        }
    }
}

// ==========================================================
// COMPONENTE: NAVBAR DINÁMICO MULTI-ROL
// ==========================================================
@Composable
fun BottomNavigationBarDynamic(rol: String, currentSelection: String = "") {
    // Definimos los botones e íconos exactos por cada rol basándonos en tu diagrama
    val (items, icons) = when (rol) {
        "QUALITY" -> Pair(
            listOf("Huariques", "Reseñas", "Perfil"),
            listOf(Icons.Outlined.LocationOn, Icons.Outlined.Star, Icons.Outlined.Person)
        )
        "TIENDA" -> Pair(
            listOf("Platos", "Métricas", "Perfil"),
            listOf(Icons.Outlined.Menu, Icons.Outlined.PlayArrow, Icons.Outlined.Person) // PlayArrow actúa como placeholder de métricas
        )
        "ADMIN" -> Pair(
            listOf("Aprobaciones", "Reportes", "Perfil"),
            listOf(Icons.Outlined.CheckCircle, Icons.Outlined.Warning, Icons.Outlined.Person)
        )
        else -> Pair(
            listOf("Home", "Ofertas", "Pedidos", "Favoritos", "Perfil"),
            listOf(Icons.Outlined.Home, Icons.Outlined.Star, Icons.Outlined.ShoppingCart, Icons.Outlined.FavoriteBorder, Icons.Outlined.Person)
        )
    }

    NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = { Icon(icons[index], contentDescription = item) },
                label = { Text(item, fontSize = 10.sp) },
                selected = index == 0, // Por diseño estructural, el primero queda marcado inicialmente
                onClick = { /* Navegación interna futura de sub-secciones */ },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFFD32F2F),
                    selectedTextColor = Color(0xFFD32F2F),
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray,
                    indicatorColor = Color.White
                )
            )
        }
    }
}

// ==========================================================
// VISTAS TEMPORALES ESTRUCTURALES
// ==========================================================
@Composable
fun PlaceholderHome(rol: String, titulo: String, subtitulo: String) {
    Scaffold(
        bottomBar = { BottomNavigationBarDynamic(rol = rol) },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(30.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Build,
                contentDescription = null,
                modifier = Modifier.size(70.dp),
                tint = Color(0xFFD32F2F)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(text = titulo, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.Black, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = subtitulo, fontSize = 14.sp, color = Color.Gray, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(30.dp))
            Text(
                text = "ESTAMOS TRABAJANDO EN ELLO",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.LightGray,
                letterSpacing = 2.sp
            )
        }
    }
}

@Composable
fun MuralTiendaBlancoScreen(idTienda: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Mural del Huarique\n\nID capturado de Firebase:\n$idTienda\n\n(Estructura lista para inyectar comentarios CHAS)",
            color = Color.DarkGray,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )
    }
}