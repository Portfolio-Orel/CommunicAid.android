package com.orels.data.remote.repository.api

import com.orels.domain.model.dto.body.create.*
import com.orels.domain.model.dto.body.update.UpdateFolderBody
import com.orels.domain.model.dto.body.update.UpdateMessageBody
import com.orels.domain.model.dto.response.*
import retrofit2.http.*

interface API {

    // Users
    @POST("/users")
    suspend fun createUser(@Body userBody: CreateUserBody)

    @GET("/users")
    suspend fun getUser(): Response<GetUserResponse>?

    // Messages
    @GET("/messages")
    suspend fun getMessages(@Header("If-None-Match") eTag: String = ""): Response<List<GetMessagesResponse>>

    @POST("/messages")
    suspend fun createMessage(@Body messageBody: CreateMessageBody): Response<List<String>>

    @PATCH("/messages")
    suspend fun updateMessage(@Body message: UpdateMessageBody)

    // Folders
    @POST("/folders")
    suspend fun createFolder(@Body folderBody: CreateFolderBody): Response<String>

    @GET("/folders")
    suspend fun getFolders(@Header("If-None-Match") eTag: String = ""): Response<List<GetFoldersResponse>>

    @PATCH("/folders")
    suspend fun updateFolder(@Body folder: UpdateFolderBody)

    @DELETE("/folders/{id}")
    suspend fun deleteFolder(@Path("id") id: String)

    // Deleted Calls
    @POST("/deletedCalls")
    suspend fun createDeletedCall(@Body deletedCallBody: CreateDeletedCallBody): Response<String?>

    @GET("/deletedCalls/{from_date}")
    suspend fun getDeletedCalls(
        @Path("from_date") fromDate: Long,
    ): Response<List<GetDeletedCallsResponse>>

    // Phone Calls
    @POST("/phoneCall")
    suspend fun createPhoneCall(@Body phoneCallBody: CreatePhoneCallBody): Response<String>

    @POST("/phoneCalls")
    suspend fun createPhoneCalls(@Body phoneCallsBody: List<CreatePhoneCallBody>): Response<List<String>>

    // Settings
    @PATCH("/settings")
    suspend fun createOrUpdateSettings(
        @Body createOrUpdateSettingsBody: List<CreateOrUpdateSettingsBody>
    )

    @GET("/settings/{key}")
    suspend fun getSettings(
        @Path("key") key: String,
        @Header("If-None-Match") vararg eTags: String
    ): Response<List<SettingsResponse>>

    @GET("/settings")
    suspend fun getAllSettings(): Response<List<SettingsResponse>>

    @DELETE("/messagesInFolders/{folder_id}")
    suspend fun deleteMessagesInFolder(@Path("folder_id") folderId: String)


    // Statistics
    @GET("/statistics/callsCount")
    suspend fun getCallsCountByType(
        @Query("start_date") startDate: Long? = null,
        @Query("end_date") endDate: Long? = null,
        @Header("If-None-Match") eTag: String = ""
        ): Response<GetCallsCountResponse>

    @GET("/statistics/messagesSentCount")
    suspend fun getMessagesSentCount(
        @Query("start_date") startDate: Long? = null,
        @Query("end_date") endDate: Long? = null,
        @Header("If-None-Match") eTag: String = ""
    ): Response<List<GetMessagesSentCountResponse>>

}