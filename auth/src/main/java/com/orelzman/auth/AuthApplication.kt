package com.orelzman.auth

import android.app.Application
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp

class AuthApplication : Application() {
    init {
        FirebaseApp.initializeApp(this)
    }
}