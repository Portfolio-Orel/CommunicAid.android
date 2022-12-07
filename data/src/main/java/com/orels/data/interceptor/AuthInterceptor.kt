package com.orels.data.interceptor

import com.orels.auth.domain.interactor.AuthInteractor
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val authInteractor: AuthInteractor,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response =
        takeIf { authInteractor.getUser()?.token != "" }
            .run { chain.request() }
            .newBuilder()
            .addHeader("Authorization", authInteractor.getUser()?.token ?: "")
            .addHeader("UserId", authInteractor.getUser()?.userId ?: "")
            .build()
            .run { chain.proceed(this) }
}