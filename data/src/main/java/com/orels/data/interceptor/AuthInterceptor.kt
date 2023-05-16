package com.orels.data.interceptor

import com.orels.auth.domain.interactor.Auth
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val auth: Auth,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response =
        with(auth.getUser()) {
            chain.request()
                .newBuilder()
                .addHeader("Authorization", this?.token ?: "")
                .addHeader("UserId", this?.userId ?: "")
                .build()
                .run { chain.proceed(this) }
        }
}
