package com.orelzman.auth.data.repository

import android.content.Context
import com.google.android.gms.auth.api.credentials.Credential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


class AuthRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {


    val user: FirebaseUser?
        get() = auth.currentUser


    fun isAuth(): Boolean = user != null

    fun auth(email: String, password: String) =
        auth.signInWithEmailAndPassword(email, password)


//    fun googleAuth(): Completable {
//
//    }

    fun saveCredentials(email: String, password: String) { // TODO ask user if wants
        val credential: Credential = Credential.Builder(email)
            .setPassword(password)
            .build()
    }

    fun signOut() {
        auth.signOut()
        saveCredentials("", "")
    }

}