package com.tech.tucalle

import android.app.Application
import com.google.firebase.FirebaseApp

class TuCalleApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}