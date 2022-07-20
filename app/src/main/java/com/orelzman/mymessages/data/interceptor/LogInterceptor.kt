package com.orelzman.mymessages.data.interceptor

import com.orelzman.mymessages.util.extension.Log
import okhttp3.Interceptor
import okhttp3.Response

class LogInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        Log.v("HTTP Request: ${response.request().url()}, isSuccessful: ${response.isSuccessful}")
        if(!response.isSuccessful) {
            Log.v("\nFailed with error: ${response.message()}")
        } else {
            Log.v("\nResponse: ${ response.peekBody(2048).string()}")
        }
        return response
    }
}