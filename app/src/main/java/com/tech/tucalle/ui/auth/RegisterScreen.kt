package com.tech.tucalle.ui.auth

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tech.tucalle.ui.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(
    tipo: String, // Recibe "USUARIO" desde el NavGraph
    onBack: () -> Unit,
    onRegisterSuccess: () -> Unit,
    authViewModel: AuthViewModel = viewModel()
) {
    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var celular by remember { mutableStateOf("") }

    var aceptoTerminos by remember { mutableStateOf(false) }
    var aceptoPromociones by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 30.dp)
            .verticalScroll(scrollState)
    ) {
        IconButton(onClick = onBack, modifier = Modifier.padding(top = 10.dp)) {
            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Atrás", modifier = Modifier.size(30.dp))
        }

        Text(text = "Regístrate", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.Black)

        if (statusMessage.isNotEmpty()) {
            Text(
                text = statusMessage,
                color = if (statusMessage.contains("éxito")) Color(0xFF4CAF50) else Color.Red,
                fontSize = 14.sp,
                modifier = Modifier.padding(vertical = 10.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        LoginInput(label = "Nombre completo*", value = nombre, onValueChange = { nombre = it })
        LoginInput(label = "Email*", value = email, onValueChange = { email = it })
        LoginInput(label = "Contraseña*", value = password, onValueChange = { password = it }, isPassword = true)
        LoginInput(label = "Número de contacto*", value = celular, onValueChange = { celular = it })

        Spacer(modifier = Modifier.height(15.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = aceptoTerminos,
                onCheckedChange = { aceptoTerminos = it },
                colors = CheckboxDefaults.colors(checkedColor = Color(0xFFD32F2F))
            )
            Text("Acepto los términos y condiciones", fontSize = 13.sp, color = Color.Black)
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = aceptoPromociones,
                onCheckedChange = { aceptoPromociones = it },
                colors = CheckboxDefaults.colors(checkedColor = Color(0xFFD32F2F))
            )
            Text("Acepto recibir promociones a mi correo", fontSize = 13.sp, color = Color.Black)
        }

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = {
                if (email.isNotEmpty() && password.isNotEmpty() && nombre.isNotEmpty()) {
                    isLoading = true
                    // Guardamos la estructura del usuario normal
                    val userData = mapOf(
                        "nombre" to nombre,
                        "email" to email,
                        "celular" to celular,
                        "rol" to tipo, // Aquí pasará "USUARIO"
                        "recibirPromociones" to aceptoPromociones
                    )

                    authViewModel.registerUserWithRole(
                        email = email,
                        pass = password,
                        userData = userData,
                        onSuccess = {
                            isLoading = false
                            statusMessage = "¡Usuario registrado con éxito!"
                            onRegisterSuccess()
                        },
                        onFailure = { error ->
                            isLoading = false
                            statusMessage = error
                        }
                    )
                } else {
                    statusMessage = "Por favor, llena los campos requeridos."
                }
            },
            enabled = aceptoTerminos && !isLoading,
            modifier = Modifier.fillMaxWidth().height(55.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
            shape = RoundedCornerShape(30.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text("Registrarse", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}