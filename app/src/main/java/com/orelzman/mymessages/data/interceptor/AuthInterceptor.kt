package com.orelzman.mymessages.data.interceptor

import com.orelzman.auth.domain.interactor.AuthInteractor
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor (
    private val authInteractor: AuthInteractor
) : Interceptor {
    private val lock = Any()
    override fun intercept(chain: Interceptor.Chain): Response =
        takeIf { synchronized(lock) { authInteractor.getUser()?.token != "" } }
            .run { chain.request() }
            .newBuilder()
            .addHeader("Authorization", authInteractor.getUser()?.token ?: "")
            .addHeader("UserId", authInteractor.getUser()?.userId ?: "")
            .build()
            .run { chain.proceed(this) }
}