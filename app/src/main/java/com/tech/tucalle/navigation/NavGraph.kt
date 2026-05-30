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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

// Pantallas Auth
import com.tech.tucalle.ui.auth.*

// Pantallas Usuario
import com.tech.tucalle.ui.usuario.HomeScreen
import com.tech.tucalle.ui.usuario.ProfileUsuarioScreen
import com.tech.tucalle.ui.usuario.StoreDetailScreen

// Pantallas Quality
import com.tech.tucalle.ui.quality.HomeQualityScreen
import com.tech.tucalle.ui.quality.ProfileQualityScreen
import com.tech.tucalle.ui.quality.NuevaEvaluacionScreen // 🔥 IMPORTACIÓN NUEVA

// Pantallas Huarique/Tienda
import com.tech.tucalle.ui.huarique.StoreHomeScreen
import com.tech.tucalle.ui.huarique.ProfileTiendaScreen
import com.tech.tucalle.ui.huarique.GestionPlatosScreen

// ViewModels
import com.tech.tucalle.ui.viewmodel.AuthViewModel

@Composable
fun NavGraph(navController: NavHostController) {
    val authViewModel: AuthViewModel = viewModel()

    NavHost(navController = navController, startDestination = "splash") {

        // ── AUTH ──────────────────────────────────────────────────
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
                onLoginSuccess = { rol ->
                    val destino = when (rol) {
                        "TIENDA" -> "home_tienda"
                        "QUALITY" -> "home_quality_main"
                        "ADMIN" -> "home_admin"
                        else -> "home_user"
                    }
                    navController.navigate(destino) {
                        popUpTo("auth") { inclusive = true }
                    }
                },
                onNavigateToForgot = { navController.navigate("forgot_password") }
            )
        }

        composable("forgot_password") {
            ForgotPasswordScreen(
                onBack = { navController.popBackStack() },
                onNavigateToVerify = { email -> navController.navigate("verify_code/$email") }
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

        // ── REGISTRO ──────────────────────────────────────────────
        composable("user_type") {
            UserTypeScreen(
                onTypeSelected = { tipo ->
                    if (tipo == "TIENDA") navController.navigate("register_store")
                    else navController.navigate("register/$tipo")
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable("register/{tipo}") { backStackEntry ->
            val tipo = backStackEntry.arguments?.getString("tipo") ?: "USUARIO"
            RegisterScreen(
                tipo = tipo,
                onBack = { navController.popBackStack() },
                onRegisterSuccess = {
                    val destino = if (tipo == "QUALITY") "home_quality_main" else "home_user"
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

        // ── HOMES ─────────────────────────────────────────────────

        composable("home_user") {
            HomeScreen(
                navController = navController,
                authViewModel = authViewModel,
                rol = "USUARIO",
                onRestaurantClick = { idTienda ->
                    navController.navigate("mural_tienda/$idTienda/USUARIO")
                }
            )
        }

        composable("home_quality_main") {
            HomeScreen(
                navController = navController,
                authViewModel = authViewModel,
                rol = "QUALITY",
                onRestaurantClick = { idTienda ->
                    navController.navigate("mural_tienda/$idTienda/QUALITY")
                }
            )
        }

        composable("home_quality") {
            HomeQualityScreen(
                navController = navController,
                onRestaurantClick = { idTienda ->
                    navController.navigate("mural_tienda/$idTienda/QUALITY")
                }
            )
        }

        composable("home_tienda") {
            StoreHomeScreen(
                authViewModel = authViewModel,
                navController = navController
            )
        }

        composable("home_admin") {
            PlaceholderHome(
                rol = "ADMIN",
                titulo = "Panel Administrativo / TI",
                subtitulo = "Sala de aprobaciones pendientes para nuevos platos y validación de comercios.",
                navController = navController
            )
        }

        // 🔥 PANTALLA NUEVA EVALUACIÓN CHAS (La que faltaba)
        composable("nueva_evaluacion") {
            NuevaEvaluacionScreen(navController = navController)
        }

        // 🔥 GESTIÓN DE PLATOS DE LA TIENDA
        composable("gestion_platos") {
            GestionPlatosScreen(navController = navController)
        }

        composable(
            route = "en_construccion/{rol}",
            arguments = listOf(navArgument("rol") { type = NavType.StringType })
        ) { backStackEntry ->
            val rol = backStackEntry.arguments?.getString("rol") ?: "USUARIO"
            PlaceholderHome(
                rol = rol,
                titulo = "Próximamente",
                subtitulo = "Esta sección estará disponible en futuras actualizaciones.",
                navController = navController
            )
        }

        // ── PERFILES ──────────────────────────────────────────────
        composable("perfil_usuario") {
            ProfileUsuarioScreen(
                navController = navController,
                onBack = { navController.popBackStack() },
                onLogout = { navController.navigate("auth") { popUpTo(0) { inclusive = true } } }
            )
        }

        composable("perfil_quality") {
            ProfileQualityScreen(
                navController = navController,
                onBack = { navController.popBackStack() },
                onLogout = { navController.navigate("auth") { popUpTo(0) { inclusive = true } } }
            )
        }

        composable("perfil_tienda") {
            ProfileTiendaScreen(
                navController = navController,
                onBack = { navController.popBackStack() },
                onLogout = { navController.navigate("auth") { popUpTo(0) { inclusive = true } } }
            )
        }

        composable("perfil") {
            ProfileUsuarioScreen(
                navController = navController,
                onBack = { navController.popBackStack() },
                onLogout = { navController.navigate("auth") { popUpTo(0) { inclusive = true } } }
            )
        }

        // ── MURAL TIENDA ──────────────────────
        composable(
            route = "mural_tienda/{idTienda}/{rol}",
            arguments = listOf(
                navArgument("idTienda") { type = NavType.StringType },
                navArgument("rol") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val idTienda = backStackEntry.arguments?.getString("idTienda") ?: ""
            val rol = backStackEntry.arguments?.getString("rol") ?: "USUARIO"

            StoreDetailScreen(navController = navController, rol = rol, idTienda = idTienda)
        }
    }
}


// =========================================================================
// COMPONENTES EXTERNOS (Fuera del NavGraph)
// =========================================================================

// ── NAVBAR DINÁMICO MEJORADO ─────────────────────────────────────────
@Composable
fun BottomNavigationBarDynamic(
    rol: String,
    currentSelection: String = "",
    navController: NavHostController? = null,
    onItemClick: (String) -> Unit = {}
) {
    val (items, icons) = when (rol) {
        "QUALITY" -> Pair(
            listOf("Home", "Reseñas", "Huariques", "Favoritos", "Perfil"),
            listOf(
                Icons.Outlined.Home,
                Icons.Outlined.Star,
                Icons.Outlined.LocationOn,
                Icons.Outlined.FavoriteBorder,
                Icons.Outlined.Person
            )
        )

        "ADMIN" -> Pair(
            listOf("Aprobaciones", "Reportes", "Perfil"),
            listOf(
                Icons.Outlined.CheckCircle,
                Icons.Outlined.Warning,
                Icons.Outlined.Person
            )
        )

        "TIENDA" -> Pair(
            listOf("Home", "Platos", "Reseñas", "Métricas", "Perfil"),
            listOf(
                Icons.Outlined.Home,
                Icons.Outlined.MenuBook,
                Icons.Outlined.Star,
                Icons.Outlined.BarChart,
                Icons.Outlined.Person
            )
        )

        else -> Pair(
            listOf("Home", "Ofertas", "Pedidos", "Favoritos", "Perfil"),
            listOf(
                Icons.Outlined.Home,
                Icons.Outlined.Star,
                Icons.Outlined.ShoppingCart,
                Icons.Outlined.FavoriteBorder,
                Icons.Outlined.Person
            )
        )
    }

    NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
        items.forEachIndexed { index, item ->
            val isSelected = currentSelection == item
            NavigationBarItem(
                icon = { Icon(icons[index], contentDescription = item) },
                label = { Text(item, fontSize = 10.sp) },
                selected = isSelected,
                onClick = {
                    if (!isSelected) {
                        if (navController != null) {
                            val ruta = when (item) {
                                "Home" -> when (rol) {
                                    "QUALITY" -> "home_quality_main"
                                    "TIENDA" -> "home_tienda"
                                    "ADMIN" -> "home_admin"
                                    else -> "home_user"
                                }

                                "Platos" -> "gestion_platos"
                                "Reseñas" -> if (rol == "QUALITY") "nueva_evaluacion" else "en_construccion/$rol"
                                "Perfil" -> when (rol) {
                                    "QUALITY" -> "perfil_quality"
                                    "TIENDA" -> "perfil_tienda"
                                    else -> "perfil_usuario"
                                }

                                else -> "en_construccion/$rol"
                            }
                            navController.navigate(ruta) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        } else {
                            onItemClick(item)
                        }
                    }
                },
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

// ── PLACEHOLDER HOME ──────────────────────────────────────────────
@Composable
fun PlaceholderHome(
    rol: String,
    titulo: String,
    subtitulo: String,
    navController: NavHostController? = null
) {
    Scaffold(
        bottomBar = {
            BottomNavigationBarDynamic(
                rol = rol,
                navController = navController
            )
        },
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
            Text(
                titulo,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                subtitulo,
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(30.dp))
            Text(
                "ESTAMOS TRABAJANDO EN ELLO",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.LightGray,
                letterSpacing = 2.sp
            )
        }
    }
}