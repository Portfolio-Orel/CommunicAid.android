package com.orels.features.customer_state.data.remote
import com.orels.features.customer_state.domain.model.CustomerState
import retrofit2.http.GET
import retrofit2.http.Query

interface API
/**
 * Created by Orel Zilberman on 30/03/2024.
 */

 {
    // Existing endpoints...

    @GET("/api/finances/customer-state")
    suspend fun getCustomerState(
        @Query("phone") phone: String
    ): CustomerState

    // Other endpoints...
}