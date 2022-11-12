package com.orels.data.interceptor

import com.orels.domain.model.exception.CouldNotRefreshTokenException
import com.orels.domain.interactors.AuthInteractor
import com.orels.domain.util.extension.log
import kotlinx.coroutines.*
import okhttp3.Interceptor
import okhttp3.Response

class ErrorInterceptor(
    private val authInteractor: AuthInteractor,
) : Interceptor {
    @OptIn(DelicateCoroutinesApi::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        var response = chain.proceed(chain.request())
        when (response.code()) {
            401 -> {
                val job = GlobalScope.async { authInteractor.refreshToken() }
                CoroutineScope(SupervisorJob()).launch {
                    try {
                        job.join()
                    } catch (e: CouldNotRefreshTokenException) {
                        e.log()
                        authInteractor.signOut()
                    }
                }
                response.close()
                response = chain.proceed(chain.request())
            }
        }
        return response
    }
}