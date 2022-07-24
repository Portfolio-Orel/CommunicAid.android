package com.orelzman.mymessages.data.interceptor

import com.orelzman.auth.domain.exception.CouldNotRefreshTokenException
import com.orelzman.auth.domain.interactor.AuthInteractor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.Response

class ErrorInterceptor(
    private val authInteractor: AuthInteractor
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        when (response.code()) {
            401 -> CoroutineScope(Dispatchers.IO).launch {
                try {
                    authInteractor.refreshToken()
                } catch(e: CouldNotRefreshTokenException) {
                    authInteractor.signOut()
                }
            }
        }
        return response
    }
}