package com.orels.auth.domain.activity_result

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.orels.auth.domain.interactor.AuthInteractor
import javax.inject.Inject

class ActivityResultContractImpl : ActivityResultContract<Int, Task<GoogleSignInAccount>?>() {

    @Inject
    lateinit var authInteractor: AuthInteractor

    override fun parseResult(resultCode: Int, intent: Intent?): Task<GoogleSignInAccount>? {
        return when (resultCode) {
            Activity.RESULT_OK -> GoogleSignIn.getSignedInAccountFromIntent(intent)
            else -> null
        }
    }

    override fun createIntent(context: Context, input: Int): Intent {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("670361895848-iv5voa8ltm7trq82uggbh1umo53vn2ng.apps.googleusercontent.com")
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(context, gso).signInIntent
    }
}