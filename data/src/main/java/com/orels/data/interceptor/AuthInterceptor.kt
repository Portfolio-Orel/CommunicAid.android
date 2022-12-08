package com.orels.data.interceptor

import com.orels.auth.domain.interactor.AuthInteractor
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val authInteractor: AuthInteractor,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response =
        runBlocking {
            authInteractor.getUser()
        }.let { user ->
            chain.request()
                .newBuilder()
                .addHeader("Authorization", user?.token ?: "")
                .addHeader("UserId", user?.userId ?: "")
                .build()
                .run { chain.proceed(this) }
        }

}