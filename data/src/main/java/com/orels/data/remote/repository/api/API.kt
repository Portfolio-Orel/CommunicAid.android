package com.orels.data.remote.repository.api

import com.orels.domain.model.dto.body.create.CreateDeletedCallBody
import com.orels.domain.model.dto.body.create.CreateFolderBody
import com.orels.domain.model.dto.body.create.CreateMessageBody
import com.orels.domain.model.dto.body.create.CreateOngoingCallBody
import com.orels.domain.model.dto.body.create.CreateOrUpdateSettingsBody
import com.orels.domain.model.dto.body.create.CreatePhoneCallBody
import com.orels.domain.model.dto.body.create.CreateUserBody
import com.orels.domain.model.dto.body.update.UpdateFolderBody
import com.orels.domain.model.dto.body.update.UpdateMessageBody
import com.orels.domain.model.dto.response.GetCallsCountResponse
import com.orels.domain.model.dto.response.GetDeletedCallsResponse
import com.orels.domain.model.dto.response.GetFoldersResponse
import com.orels.domain.model.dto.response.GetMessagesResponse
import com.orels.domain.model.dto.response.GetMessagesSentCountResponse
import com.orels.domain.model.dto.response.GetUserResponse
import com.orels.domain.model.dto.response.Response
import com.orels.domain.model.dto.response.SettingsResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface API {

    // Users
    @POST("/users")
    suspend fun createUser(@Body userBody: CreateUserBody)

    @GET("/users")
    suspend fun getUser(): Response<GetUserResponse>?

    // Messages
    @GET("/messages")
    suspend fun getMessages(@Header("If-None-Match") vararg eTags: String): Response<List<GetMessagesResponse>>

    @POST("/messages")
    suspend fun createMessage(@Body messageBody: CreateMessageBody): Response<List<String>>

    @PATCH("/messages")
    suspend fun updateMessage(@Body message: UpdateMessageBody)

    // Folders
    @POST("/folders")
    suspend fun createFolder(@Body folderBody: CreateFolderBody): Response<String>

    @GET("/folders")
    suspend fun getFolders(@Header("If-None-Match") vararg eTags: String): Response<List<GetFoldersResponse>>

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
    @POST("/phoneCalls")
    suspend fun createPhoneCalls(@Body phoneCallsBody: List<CreatePhoneCallBody>): Response<List<String>>

    // Ongoing Calls
    @POST("/ongoing-call")
    suspend fun createOngoingCall(
        @Body createOngoingCallBody: CreateOngoingCallBody
    )

    @DELETE("/ongoing-call")
    suspend fun clearOngoingCall()

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
        @Header("If-None-Match") vararg eTags: String
    ): Response<GetCallsCountResponse>

    @GET("/statistics/messagesSentCount")
    suspend fun getMessagesSentCount(
        @Query("start_date") startDate: Long? = null,
        @Query("end_date") endDate: Long? = null,
        @Header("If-None-Match") vararg eTags: String
    ): Response<List<GetMessagesSentCountResponse>>

}