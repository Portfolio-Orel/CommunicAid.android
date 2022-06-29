package com.orelzman.mymessages.data.remote.repository.api

import retrofit2.http.POST

interface API {

    @POST
    fun createUser()
}