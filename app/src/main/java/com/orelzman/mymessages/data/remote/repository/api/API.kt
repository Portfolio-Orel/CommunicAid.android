package com.orelzman.mymessages.data.remote.repository.api

import com.orelzman.mymessages.data.remote.repository.dto.*
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface API {

    // Users
    @POST("/users")
    suspend fun createUser(@Body userBody: CreateUserBody)

    @GET("/users/{user_id}")
    suspend fun getUser(@Path("user_id") userId: String): GetUserResponse

    // Messages
    @POST("/messages")
    suspend fun createMessage(@Body messageBody: CreateMessageBody): String

    @GET("/messages/{user_id}")
    suspend fun getMessages(@Path("user_id") userId: String): List<GetMessagesResponse>

    // Folders
    @POST("/folders")
    suspend fun createFolder(@Body folderBody: CreateFolderBody): String

    @GET("/folders/{user_id}")
    suspend fun getFolders(@Path("user_id") userId: String): List<GetFoldersResponse>

    // Deleted Calls
    @POST("/deletedCalls")
    suspend fun createDeletedCall(@Body deletedCallBody: CreateDeletedCallBody): String

    @GET("/deletedCalls/{user_id}")
    suspend fun getDeletedCalls(@Path("user_id") userId: String): List<GetDeletedCallsResponse>

    // Phone Calls
    @POST("/phoneCalls")
    suspend fun createPhoneCall(phoneCallBody: CreatePhoneCallBody): String
}