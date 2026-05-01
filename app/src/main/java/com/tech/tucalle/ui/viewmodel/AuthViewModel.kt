package com.tech.tucalle.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

// Crea este archivo en: ui/auth/AuthViewModel.kt
class AuthViewModel : ViewModel() {
    private val auth = Firebase.auth

    fun loginUser(email: String, pass: String, onSuccess: () -> Unit) {
        if (email.isNotEmpty() && pass.isNotEmpty()) {
            auth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) onSuccess()
                    else { /* Aquí manejas el error, p.ej. clave mal puesta */ }
                }
        }
    }
}