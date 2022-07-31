package com.orelzman.mymessages.data.interceptor

import com.orelzman.auth.domain.exception.CouldNotRefreshTokenException
import com.orelzman.auth.domain.interactor.AuthInteractor
import com.orelzman.mymessages.util.extension.log
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class ErrorInterceptor(
    private val authInteractor: AuthInteractor
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var response = chain.proceed(chain.request())
        when (response.code()) {
            401 -> {
                runBlocking {
                    try {
                        authInteractor.refreshToken()
                    } catch (e: CouldNotRefreshTokenException) {
                        e.log()
                        authInteractor.signOut()
                    }
                    response.close()
                    response = chain.proceed(chain.request())
                }
            }
        }
        return response
    }
}