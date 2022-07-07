package com.orelzman.mymessages.data.remote

import com.orelzman.auth.domain.interactor.AuthInteractor
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor (
    private val authInteractor: AuthInteractor
) : Interceptor {

    private val lock = Any()
// ToDo: Add user_id to here and remove from requests.
    override fun intercept(chain: Interceptor.Chain): Response =
        takeIf { synchronized(lock) { authInteractor.getToken() != "" } }
            .run { chain.request() }
            .newBuilder()
            .addHeader("Authorization", authInteractor.getToken())
            .build()
            .run { chain.proceed(this) }
}