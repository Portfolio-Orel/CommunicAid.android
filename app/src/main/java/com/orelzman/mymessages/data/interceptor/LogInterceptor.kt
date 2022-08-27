package com.orelzman.mymessages.data.interceptor

import com.orelzman.mymessages.domain.util.extension.Logger
import okhttp3.Interceptor
import okhttp3.Response
import okio.Buffer

class LogInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)
        val buffer = Buffer()
        request.body()?.writeTo(buffer)
        Logger.v("HTTP Request: ${response.request().url()}," +
                " method: ${response.request().method()}," +
                " status: ${response.code()}," +
                " request body: ${buffer.readUtf8()}")
        if(!response.isSuccessful) {
            Logger.e("\nFailed with error: ${response.message()}")
        } else {
            Logger.v("\nResponse: ${ response.peekBody(2048).string()}")
        }
        return response
    }
}