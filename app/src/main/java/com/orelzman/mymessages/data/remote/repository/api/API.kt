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

    @GET("/users")
    suspend fun getUser(): Response<GetUserResponse>?

    // Messages
    @GET("/messages")
    suspend fun getMessages(): Response<List<GetMessagesResponse>>

    @POST("/messages")
    suspend fun createMessage(@Body messageBody: CreateMessageBody): Response<List<String>>

    @PATCH("/messages")
    suspend fun updateMessage(@Body message: UpdateMessageBody)

    // Folders
    @POST("/folders")
    suspend fun createFolder(@Body folderBody: CreateFolderBody): Response<String>

    @GET("/folders")
    suspend fun getFolders(): Response<List<GetFoldersResponse>>

    @PATCH("/folders")
    suspend fun updateFolder(@Body folder: UpdateFolderBody)

    // Deleted Calls
    @POST("/deletedCalls")
    suspend fun createDeletedCall(@Body deletedCallBody: CreateDeletedCallBody): Response<String>

    @GET("/deletedCalls")
    suspend fun getDeletedCalls(): Response<List<GetDeletedCallsResponse>>

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

    @GET("/settings/{key}")
    suspend fun getSettings(
        @Path("key") key: String,
    ): Response<List<SettingsResponse>>

    @GET("/settings")
    suspend fun getAllSettings(): Response<List<SettingsResponse>>

    @DELETE("/messagesInFolders/{folder_id}")
    suspend fun deleteMessagesInFolder(@Path("folder_id") folderId: String)


    // Statistics
    @GET("/statistics/callsCount")
    suspend fun getCallsCountByType(): Response<GetCallsCountResponse>

    @GET("/statistics/messagesSentCount")
    suspend fun getMessagesSentCount(): Response<List<GetMessagesSentCountResponse>>

}