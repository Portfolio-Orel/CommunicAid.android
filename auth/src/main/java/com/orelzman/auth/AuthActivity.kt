package com.orelzman.auth

import androidx.activity.ComponentActivity
import com.google.firebase.FirebaseApp

class AuthActivity : ComponentActivity() {
    init {
        FirebaseApp.initializeApp(this)
    }
}