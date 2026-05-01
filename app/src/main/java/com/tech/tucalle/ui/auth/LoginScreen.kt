package com.tech.tucalle.ui.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
// Estas son las importaciones correctas para Firebase Slark
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@Composable
fun LoginScreen(onNavigateToRegister: () -> Unit, onBack: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var statusMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(30.dp)
            .background(Color.White)
    ) {
        IconButton(onClick = onBack, modifier = Modifier.padding(top = 10.dp)) {
            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Atrás", modifier = Modifier.size(30.dp))
        }

        Spacer(modifier = Modifier.height(40.dp))

        Text(text = "Inicia sesión", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.Black)

        if (statusMessage.isNotEmpty()) {
            Text(
                text = statusMessage,
                color = if (statusMessage.contains("enviado")) Color(0xFF4CAF50) else Color.Red,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 10.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Estas son las que daban error porque faltaban abajo
        LoginInput(label = "Email*", value = email, onValueChange = { email = it })
        LoginInput(label = "Contraseña*", value = password, onValueChange = { password = it }, isPassword = true)

        TextButton(
            onClick = {
                if (email.isNotEmpty()) {
                    Firebase.auth.sendPasswordResetEmail(email)
                        .addOnCompleteListener { task ->
                            statusMessage = if (task.isSuccessful) "Correo de recuperación enviado"
                            else "Error: ${task.exception?.message}"
                        }
                } else {
                    statusMessage = "Escribe tu email para recuperar la clave"
                }
            }
        ) {
            Text(text = "Olvidé mi contraseña", fontSize = 12.sp, textDecoration = TextDecoration.Underline, color = Color.Black)
        }

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = {
                if (email.isNotEmpty() && password.isNotEmpty()) {
                    Firebase.auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                println("Logueado con éxito en Slark")
                            } else {
                                statusMessage = "Error al iniciar sesión"
                            }
                        }
                }
            },
            modifier = Modifier.fillMaxWidth().height(55.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
            shape = RoundedCornerShape(30.dp)
        ) {
            Text(text = "Ingresar", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        LoginDivider() // Esta también daba error

        OutlinedButton(
            onClick = onNavigateToRegister,
            modifier = Modifier.fillMaxWidth().height(55.dp),
            border = BorderStroke(1.dp, Color(0xFFD32F2F)),
            shape = RoundedCornerShape(30.dp)
        ) {
            Text(text = "Registrarse", color = Color(0xFFD32F2F), fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
    }
}

// FUNCIONES AUXILIARES (Tienen que estar aquí para que no salgan errores)

@Composable
fun LoginInput(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isPassword: Boolean = false
) {
    Column(modifier = Modifier.padding(vertical = 10.dp)) {
        Row {
            Text(text = label.replace("*", ""), fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
            if (label.contains("*")) {
                Text(text = "*", color = Color.Red, fontSize = 16.sp)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp)
                .shadow(elevation = 8.dp, shape = RoundedCornerShape(12.dp), clip = false),
            shape = RoundedCornerShape(12.dp),
            color = Color.White
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxSize(),
                visualTransformation = if (isPassword) androidx.compose.ui.text.input.PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color(0xFFD32F2F),
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
        }
    }
}

@Composable
fun LoginDivider() {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 30.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HorizontalDivider(modifier = Modifier.weight(1f), thickness = 1.dp, color = Color.LightGray.copy(alpha = 0.5f))
        Text(
            text = "¿No tienes una cuenta?",
            modifier = Modifier.padding(horizontal = 10.dp),
            fontSize = 12.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
        HorizontalDivider(modifier = Modifier.weight(1f), thickness = 1.dp, color = Color.LightGray.copy(alpha = 0.5f))
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen(onNavigateToRegister = {}, onBack = {})
}