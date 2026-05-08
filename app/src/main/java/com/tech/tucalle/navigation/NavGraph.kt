package com.tech.tucalle.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.tech.tucalle.ui.auth.AuthScreen
import com.tech.tucalle.ui.auth.LoginScreen
import com.tech.tucalle.ui.auth.RegisterScreen
import com.tech.tucalle.ui.auth.RegisterStoreScreen // Tu nueva pantalla de Tienda
import com.tech.tucalle.ui.auth.SplashScreen
import com.tech.tucalle.ui.auth.UserTypeScreen
import com.tech.tucalle.ui.viewmodel.AuthViewModel

// Pantallas de marcador de posición temporales (puedes reemplazarlas por tus archivos reales luego)
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun NavGraph(navController: NavHostController) {
    // Instanciamos el AuthViewModel para compartirlo en la navegación si es necesario
    val authViewModel: AuthViewModel = viewModel()

    NavHost(navController = navController, startDestination = "splash") {

        // 1. Carga inicial (Splash)
        composable("splash") {
            SplashScreen(onNavigateToLogin = {
                navController.navigate("auth") {
                    popUpTo("splash") { inclusive = true }
                }
            })
        }

        // 2. Pantalla de bienvenida (AuthScreen)
        composable("auth") {
            AuthScreen(
                onNavigateToLogin = { navController.navigate("login") },
                onNavigateToGoogle = {
                    // Lógica de Google: se registra automáticamente como "USUARIO"
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

        // 3. Iniciar Sesión (Decide a dónde ir según el Rol que traiga de Firestore)
        composable("login") {
            LoginScreen(
                onNavigateToRegister = { navController.navigate("user_type") },
                onBack = { navController.popBackStack() },
                onLoginSuccess = { rol ->
                    // ¡Aquí ocurre la magia! Dependiendo del rol, renderiza una pantalla u otra
                    val destino = if (rol == "TIENDA") "dashboard_store" else "home_user"
                    navController.navigate(destino) {
                        popUpTo("auth") { inclusive = true } // Limpia el historial para que no retroceda al login
                    }
                }
            )
        }

        // 4. Selección de Rol (Soy Usuario / Soy Tienda)
        composable("user_type") {
            UserTypeScreen(
                onTypeSelected = { tipo ->
                    // Si eligen "USUARIO", van al registro de usuario normal
                    // Si eligen "TIENDA", van al registro de tienda con más campos
                    if (tipo == "TIENDA") {
                        navController.navigate("register_store")
                    } else {
                        navController.navigate("register/USUARIO")
                    }
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        // 5. Registro Final para Clientes (Rol: USUARIO)
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

        // 6. Registro Final para Tiendas (Rol: TIENDA)
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

        // 7. Pantalla del Cliente (Home del Usuario)
        composable("home_user") {
            HomeScreen(onLogout = {
                navController.navigate("auth") {
                    popUpTo(0) // Limpia todo el historial de navegación al salir
                }
            })
        }

        // 8. Pantalla del Administrador (Dashboard de la Tienda)
        composable("dashboard_store") {
            StoreDashboardScreen(onLogout = {
                navController.navigate("auth") {
                    popUpTo(0)
                }
            })
        }
    }
}

// Componibles de prueba rápidos para que compile sin errores antes de que crees las pantallas reales:
@Composable
fun HomeScreen(onLogout: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("¡Bienvenido Usuario! Aquí verás la lista de tiendas de tu calle.")
    }
}

@Composable
fun StoreDashboardScreen(onLogout: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("¡Bienvenido Socio Tienda! Aquí gestionarás tus productos y horarios.")
    }
}