package com.orelzman.auth.data.repository

import com.google.android.gms.auth.api.credentials.Credential
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider


class AuthRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    val signInRequest: BeginSignInRequest = BeginSignInRequest.builder()
        .setGoogleIdTokenRequestOptions(
            BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                .setSupported(true)
                // Your server's client ID, not your Android client ID.
                .setServerClientId("670361895848-0jildiu2ebiip55tqnkdtuhm1oq5mujc.apps.googleusercontent.com")
                // Only show accounts previously used to sign in.
                .setFilterByAuthorizedAccounts(true)
                .build()
        )
        .build()

    val user: FirebaseUser?
        get() = auth.currentUser


    fun isAuth(): Boolean = user != null

    fun auth(email: String, password: String) =
        auth.signInWithEmailAndPassword(email, password)


    fun googleAuth(idToken: String): Task<AuthResult> {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        return auth.signInWithCredential(credential)
    }

    fun saveCredentials(email: String, password: String): Credential =
        Credential.Builder(email)
            .setPassword(password)
            .build()


    fun signOut() {
        auth.signOut()
        saveCredentials("", "")
    }

}