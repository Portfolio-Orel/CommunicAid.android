package com.orelzman.auth.data.repository

import com.google.android.gms.auth.api.credentials.Credential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class AuthRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    val user: FirebaseUser?
        get() = auth.currentUser


    fun isAuth(): Boolean = user != null

    fun auth(email: String, password: String) =
        auth.signInWithEmailAndPassword(email, password)


//    fun googleAuth(): Completable {
//
//    }

    fun saveCredentials(email: String, password: String) =
        Credential.Builder(email)
            .setPassword(password)
            .build()


    fun signOut() {
        auth.signOut()
        saveCredentials("", "")
    }

}