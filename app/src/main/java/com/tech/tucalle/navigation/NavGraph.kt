package com.tech.tucalle.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.tech.tucalle.ui.auth.AuthScreen
import com.tech.tucalle.ui.auth.LoginScreen
import com.tech.tucalle.ui.auth.RegisterScreen
import com.tech.tucalle.ui.auth.SplashScreen
import com.tech.tucalle.ui.auth.UserTypeScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "splash") {

        // 1. Carga inicial
        composable("splash") {
            SplashScreen(onNavigateToLogin = {
                navController.navigate("auth") {
                    popUpTo("splash") { inclusive = true }
                }
            })
        }

        // 2. Pantalla con fondo de GitHub
        composable("auth") {
            AuthScreen(
                onNavigateToLogin = { navController.navigate("login") },
                onNavigateToGoogle = { /* Lógica Google */ }
            )
        }

        // 3. Iniciar Sesión
        composable("login") {
            LoginScreen(
                onNavigateToRegister = { navController.navigate("user_type") },
                onBack = { navController.popBackStack() }
            )
        }

        // 4. Selección de Rol (Asegúrate de que los nombres coincidan con UserTypeScreen)
        composable("user_type") {
            UserTypeScreen(
                onTypeSelected = { tipo ->
                    navController.navigate("register/$tipo")
                },
                onBack = { // <-- Asegúrate que diga onBack
                    navController.popBackStack()
                }
            )
        }

        // 5. Registro Final
        composable("register/{tipo}") { backStackEntry ->
            val tipo = backStackEntry.arguments?.getString("tipo") ?: "USUARIO"
            RegisterScreen(
                tipo = tipo,
                onBack = { navController.popBackStack() }
            )
        }
    }
}