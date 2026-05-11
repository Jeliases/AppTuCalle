package com.tech.tucalle.ui.auth

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Firebase
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.tech.tucalle.R

@Composable
fun AuthScreen(onNavigateToLogin: () -> Unit, onNavigateToGoogle: () -> Unit) {

// --- LÓGICA DE FIREBASE ---
    val context = LocalContext.current

// REEMPLAZA ESTA LÍNEA CON TU NUEVO ID
    val webClientId = "117716463919-989dsnra1uunh027h4f3lc8m4324tlgf.apps.googleusercontent.com"

    val gso = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(webClientId) // Ahora sí usará el ID correcto
            .requestEmail()
            .build()
    }
    val googleSignInClient = remember { GoogleSignIn.getClient(context, gso) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            Firebase.auth.signInWithCredential(credential).addOnCompleteListener { taskAuth ->
                if (taskAuth.isSuccessful) {
                    // ¡ÉXITO! Navega al Home
                    onNavigateToGoogle()
                }
            }
        } catch (e: Exception) {
            println("Error en Google Sign-In: ${e.message}")
        }
    }
    // ---------------------------------

    Box(modifier = Modifier.fillMaxSize()) {
        AsyncImage(
            model = "https://raw.githubusercontent.com/Jeliases/AppTuCalle/refs/heads/main/ImagesTuCalle/backGround.png",
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)))

        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 30.dp, vertical = 50.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            Text(text = "DESCUBRE UN\nNUEVO MUNDO", color = Color.White, fontSize = 42.sp, lineHeight = 48.sp, fontWeight = FontWeight.Black, textAlign = TextAlign.Center)
            Text(text = "más cerca de ti", color = Color.White, fontSize = 20.sp, modifier = Modifier.padding(top = 8.dp, bottom = 40.dp))

            // BOTÓN ROJO: EMAIL (Manual)
            Button(
                onClick = onNavigateToLogin,
                modifier = Modifier.fillMaxWidth().height(55.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                shape = RoundedCornerShape(30.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Email, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = "Continua con email", color = Color.White, fontSize = 18.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // BOTÓN BLANCO: GMAIL (Google Login)
            Button(
                onClick = { launcher.launch(googleSignInClient.signInIntent) }, // LANZA GOOGLE
                modifier = Modifier.fillMaxWidth().height(55.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                shape = RoundedCornerShape(30.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_google_logo),
                        contentDescription = "Google Logo",
                        modifier = Modifier.size(20.dp),
                        tint = Color.Unspecified
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = "Continua con gmail", color = Color(0xFFD32F2F), fontSize = 18.sp, fontWeight = FontWeight.Medium)
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            Text(text = "Al iniciar sesión estas de acuerdo con nuestros", color = Color.White, fontSize = 11.sp)
            Row {
                Text(text = "Términos y Condiciones", color = Color.White, fontSize = 11.sp, textDecoration = TextDecoration.Underline)
                Text(text = " y nuestra ", color = Color.White, fontSize = 11.sp)
                Text(text = "Política de Privacidad", color = Color.White, fontSize = 11.sp, textDecoration = TextDecoration.Underline)
            }
        }
    }
}