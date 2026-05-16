package com.tech.tucalle.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

// Importaciones de tus pantallas
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
                onLoginSuccess = { rol ->
                    val destino = if (rol == "TIENDA") "dashboard_store" else "home_user"
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

        composable("user_type") {
            UserTypeScreen(
                onTypeSelected = { tipo ->
                    if (tipo == "TIENDA") {
                        navController.navigate("register_store")
                    } else {
                        navController.navigate("register/USUARIO")
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
                onRegisterSuccess = {
                    navController.navigate("home_user") {
                        popUpTo("auth") { inclusive = true }
                    }
                }
            )
        }

        composable("register_store") {
            RegisterStoreScreen(
                onBack = { navController.popBackStack() },
                onNavigateToStoreDashboard = {
                    navController.navigate("dashboard_store") {
                        popUpTo("auth") { inclusive = true }
                    }
                }
            )
        }

        // --- RUTA 1: HOME DEL USUARIO (Escucha los clics) ---
        composable("home_user") {
            HomeScreen(
                authViewModel = authViewModel, // Le pasamos el ViewModel por si lo necesita
                onRestaurantClick = { idTienda ->
                    // Cuando haces clic, viaja a la nueva pantalla pasando el ID
                    navController.navigate("mural_tienda/$idTienda")
                }
            )
        }

        // --- RUTA 2: PANTALLA EN BLANCO DEL MURAL DE LA TIENDA ---
        composable(
            route = "mural_tienda/{idTienda}",
            arguments = listOf(navArgument("idTienda") { type = NavType.StringType })
        ) { backStackEntry ->
            val idTienda = backStackEntry.arguments?.getString("idTienda") ?: ""
            MuralTiendaBlancoScreen(idTienda = idTienda)
        }

        // --- RUTA 3: DASHBOARD DE LA TIENDA (Ajustes de El Mochi) ---
        composable("dashboard_store") {
            StoreHomeScreen(authViewModel = authViewModel)
        }
    }
}

// =================================================================
// PANTALLA TEMPORAL EN BLANCO (Se reemplazará en la siguiente fase)
// =================================================================
@Composable
fun MuralTiendaBlancoScreen(idTienda: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Mural de la tienda en construcción\n\nID del Huarique:\n$idTienda",
            color = Color.DarkGray,
            textAlign = TextAlign.Center
        )
    }
}