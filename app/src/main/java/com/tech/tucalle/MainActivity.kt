package com.tech.tucalle

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.tech.tucalle.navigation.NavGraph
import com.tech.tucalle.ui.theme.AppTuCalleTheme
// Importación necesaria
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)

        setContent {
            AppTuCalleTheme {
                val navController = rememberNavController()
                NavGraph(navController = navController)
            }
        }
    }
}