package com.orelzman.mymessages.data.remote

import com.orelzman.auth.domain.interactor.AuthInteractor
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor (
    private val authInteractor: AuthInteractor
) : Interceptor {

    private val lock = Any()
    override fun intercept(chain: Interceptor.Chain): Response =
        takeIf { synchronized(lock) { authInteractor.getUserSync()?.token != "" } }
            .run { chain.request() }
            .newBuilder()
            .addHeader("Authorization", authInteractor.getUserSync()?.token ?: "")
            .addHeader("UserId", authInteractor.getUserSync()?.userId ?: "")
            .build()
            .run { chain.proceed(this) }
}