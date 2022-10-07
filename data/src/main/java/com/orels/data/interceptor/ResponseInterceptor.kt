package com.orels.data.interceptor

import com.google.gson.Gson
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.Response
import okhttp3.ResponseBody
import org.json.JSONObject

class ResponseInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        val responseString: String = response.body()?.string() ?: ""
        if (response.isSuccessful) {
            val newResponse = response.newBuilder()
            val body = JSONObject(responseString)["body"]
            val jsonBody = Gson().toJson(body)
            if (jsonBody != null) {
                val contentType = response.header("Content-Type") ?: "application/json; charset=utf-8"
                newResponse.body(ResponseBody.create(MediaType.parse(contentType), jsonBody))
                return newResponse.build()
            }
        }
        return response
    }
}