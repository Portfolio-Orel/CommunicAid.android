package com.orels.data.interceptor

import com.google.gson.Gson
import com.orels.domain.util.extension.log
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.Response
import okhttp3.ResponseBody
import okio.Buffer

class ResponseInterceptor(private val gson: Gson = Gson()) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        val responseString: String = response.body()?.string() ?: ""
        if (response.isSuccessful) {
            try {
                val newResponse = response.newBuilder()
                val responseBody = Gson().fromJson(responseString, com.orels.domain.model.dto.response.Response::class.java)
                if (responseBody != null) {
                    val contentType =
                        response.header("Content-Type") ?: "application/json; charset=utf-8"
                    responseBody.eTag = response.header("etag") ?: ""
                    val jsonBody = gson.toJson(responseBody)
                    newResponse.body(ResponseBody.create(MediaType.parse(contentType), jsonBody))
                    return newResponse.build()
                }
            } catch (e: Exception) {
                val request = chain.request()
                val buffer = Buffer()
                request.body()?.writeTo(buffer)
                e.log(values =
                    mapOf(
                        "HTTP Request" to "${response.request().url()}",
                        "method" to response.request().method(),
                        "status" to "${response.code()}",
                        "request body" to buffer.readUtf8()
                    )
                )
                return response
            }
        }
        return response
    }
}