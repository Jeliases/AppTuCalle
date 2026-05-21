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

// Pantallas Auth (NO TOCAR)
import com.tech.tucalle.ui.auth.*

// Pantallas Usuario
import com.tech.tucalle.ui.usuario.HomeScreen
import com.tech.tucalle.ui.usuario.ProfileUsuarioScreen

// Pantallas Quality
import com.tech.tucalle.ui.quality.HomeQualityScreen
import com.tech.tucalle.ui.quality.ProfileQualityScreen

// Pantallas Huarique/Tienda
import com.tech.tucalle.ui.huarique.StoreHomeScreen
import com.tech.tucalle.ui.huarique.ProfileTiendaScreen

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
                        "TIENDA"  -> "home_tienda"
                        "QUALITY" -> "home_quality"
                        "ADMIN"   -> "home_admin"
                        else      -> "home_user"
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

        // ── HOMES ─────────────────────────────────────────────────

        // A. HOME USUARIO
        composable("home_user") {
            HomeScreen(
                navController = navController,
                authViewModel = authViewModel,
                onRestaurantClick = { idTienda ->
                    navController.navigate("mural_tienda/$idTienda")
                }
            )
        }

        // B. HOME QUALITY — pantalla real con CHAS y dropdown
        composable("home_quality") {
            HomeQualityScreen(navController = navController)
        }

        // C. HOME TIENDA
        composable("home_tienda") {
            StoreHomeScreen(
                authViewModel = authViewModel,
                navController = navController
            )
        }

        // D. HOME ADMIN
        composable("home_admin") {
            PlaceholderHome(
                rol = "ADMIN",
                titulo = "Panel Administrativo / TI",
                subtitulo = "Sala de aprobaciones pendientes para nuevos platos y validación de comercios."
            )
        }

        // ── PERFILES ──────────────────────────────────────────────

        // Perfil Usuario (ruta desde navbar del HomeScreen)
        composable("perfil_usuario") {
            ProfileUsuarioScreen(
                onBack = { navController.popBackStack() },
                onLogout = {
                    navController.navigate("auth") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // Perfil Quality
        composable("perfil_quality") {
            ProfileQualityScreen(
                onBack = { navController.popBackStack() },
                onLogout = {
                    navController.navigate("auth") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // Perfil Tienda (abierto desde navbar de StoreHomeScreen)
        composable("perfil_tienda") {
            ProfileTiendaScreen(
                onBack = { navController.popBackStack() },
                onLogout = {
                    navController.navigate("auth") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // Ruta legacy "perfil" → redirige al perfil de usuario
        composable("perfil") {
            ProfileUsuarioScreen(
                onBack = { navController.popBackStack() },
                onLogout = {
                    navController.navigate("auth") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // ── MURAL TIENDA ──────────────────────────────────────────
        composable(
            route = "mural_tienda/{idTienda}",
            arguments = listOf(navArgument("idTienda") { type = NavType.StringType })
        ) { backStackEntry ->
            val idTienda = backStackEntry.arguments?.getString("idTienda") ?: ""
            MuralTiendaBlancoScreen(idTienda = idTienda)
        }
    }
}

// ── NAVBAR DINÁMICO (Quality y Admin siguen usándolo) ─────────────
@Composable
fun BottomNavigationBarDynamic(
    rol: String,
    currentSelection: String = "",
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
                onClick = { onItemClick(item) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor   = Color(0xFFD32F2F),
                    selectedTextColor   = Color(0xFFD32F2F),
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray,
                    indicatorColor      = Color.White
                )
            )
        }
    }
}

// ── PLACEHOLDER HOME ──────────────────────────────────────────────
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
            Text(titulo, fontSize = 22.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(12.dp))
            Text(subtitulo, fontSize = 14.sp, color = Color.Gray, textAlign = TextAlign.Center)
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

// ── MURAL PLACEHOLDER ─────────────────────────────────────────────
@Composable
fun MuralTiendaBlancoScreen(idTienda: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Mural del Huarique\n\nID: $idTienda\n\n(Próximamente: reseñas CHAS y platos)",
            color = Color.DarkGray,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )
    }
}

// ── BOTTOM NAV ITEMS (para HomeScreen usuario) ────────────────────
sealed class BottomNavItem(val route: String, val title: String, val icon: ImageVector) {
    object Home      : BottomNavItem("home_user",      "Home",      Icons.Outlined.Home)
    object Ofertas   : BottomNavItem("ofertas",        "Ofertas",   Icons.Outlined.Star)
    object Pedidos   : BottomNavItem("pedidos",        "Pedidos",   Icons.Outlined.ShoppingCart)
    object Favoritos : BottomNavItem("favoritos",      "Favoritos", Icons.Outlined.FavoriteBorder)
    object Perfil    : BottomNavItem("perfil_usuario", "Perfil",    Icons.Outlined.Person)
}