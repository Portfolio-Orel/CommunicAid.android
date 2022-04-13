package com.orelzman.auth

import androidx.activity.ComponentActivity
import com.google.firebase.FirebaseApp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActivity : ComponentActivity() {
    init {
        FirebaseApp.initializeApp(this)

    }
}