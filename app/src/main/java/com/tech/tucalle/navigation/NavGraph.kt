package com.tech.tucalle.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.tech.tucalle.ui.auth.*
// IMPORTANTE: Aquí estamos importando tu nuevo HomeScreen real
import com.tech.tucalle.ui.usuario.HomeScreen
import com.tech.tucalle.ui.viewmodel.AuthViewModel
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

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

        // Aquí ya usa el HomeScreen real que importamos arriba
        composable("home_user") { HomeScreen() }

        composable("dashboard_store") { StoreDashboardScreen() }
    }
}

// Ya borramos el HomeScreen de mentira, solo dejamos el de la Tienda por ahora
@Composable fun StoreDashboardScreen() { Box(Modifier.fillMaxSize(), Alignment.Center) { Text("Dashboard Tienda") } }