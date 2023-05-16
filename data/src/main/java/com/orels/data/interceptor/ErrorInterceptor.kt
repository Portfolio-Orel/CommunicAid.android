package com.orels.data.interceptor

import com.orels.auth.domain.interactor.Auth
import com.orels.domain.model.exception.CouldNotRefreshTokenException
import com.orels.domain.util.extension.log
import kotlinx.coroutines.*
import okhttp3.Interceptor
import okhttp3.Response

class ErrorInterceptor(
    private val auth: Auth,
) : Interceptor {
    @OptIn(DelicateCoroutinesApi::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        var response = chain.proceed(chain.request())
        when (response.code()) {
            401 -> {
                runBlocking {
                    auth.refreshToken()
                }
                val job = GlobalScope.async { auth.refreshToken() }
                CoroutineScope(SupervisorJob()).launch {
                    try {
                        job.join()
                    } catch (e: CouldNotRefreshTokenException) {
                        e.log()
                    }
                }
                response.close()
                response = chain.proceed(chain.request())
            }
        }
        return response
    }
}