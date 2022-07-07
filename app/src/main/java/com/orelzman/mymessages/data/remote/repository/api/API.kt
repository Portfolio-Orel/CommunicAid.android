package com.orelzman.mymessages.data.remote.repository.api

import com.orelzman.mymessages.domain.model.dto.body.create.*
import com.orelzman.mymessages.domain.model.dto.body.update.UpdateFolderBody
import com.orelzman.mymessages.domain.model.dto.body.update.UpdateMessageBody
import com.orelzman.mymessages.domain.model.dto.response.*
import retrofit2.http.*

interface API {

    // Users
    @POST("/users")
    suspend fun createUser(@Body userBody: CreateUserBody)

    @GET("/users/{user_id}")
    suspend fun getUser(@Path("user_id") userId: String): Response<GetUserResponse>?

    // Messages
    @POST("/messages")
    suspend fun createMessage(@Body messageBody: CreateMessageBody): Response<String>

    @GET("/messages/{user_id}")
    suspend fun getMessages(@Path("user_id") userId: String): Response<List<GetMessagesResponse>>

    @PATCH("/messages")
    suspend fun updateMessage(@Body message: UpdateMessageBody)

    // Folders
    @POST("/folders")
    suspend fun createFolder(@Body folderBody: CreateFolderBody): Response<String>

    @GET("/folders/{user_id}")
    suspend fun getFolders(@Path("user_id") userId: String): Response<List<GetFoldersResponse>>

    @PATCH("/folders")
    suspend fun updateFolder(@Body folder: UpdateFolderBody)

    // Deleted Calls
    @POST("/deletedCalls")
    suspend fun createDeletedCall(@Body deletedCallBody: CreateDeletedCallBody): Response<String>

    @GET("/deletedCalls/{user_id}")
    suspend fun getDeletedCalls(@Path("user_id") userId: String): Response<List<GetDeletedCallsResponse>>

    // Phone Calls
    @POST("/phoneCall")
    suspend fun createPhoneCall(@Body phoneCallBody: CreatePhoneCallBody): Response<String>

    @POST("/phoneCalls")
    suspend fun createPhoneCalls(@Body phoneCallsBody: List<CreatePhoneCallBody>): Response<List<String>>

    // Settings
    @PATCH("/settings")
    suspend fun createOrUpdateSettings(
        @Body createOrUpdateSettingsBody: CreateOrUpdateSettingsBody
    )

    @GET("/settings/{user_id}/{key}")
    suspend fun getSettings(
        @Path("user_id") userId: String,
        @Path("key") key: String,
    ): Response<List<SettingsResponse>>
}