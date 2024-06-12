package com.team2.chitchat.data.repository.remote.backend

import com.team2.chitchat.data.repository.remote.request.users.RegisterUserRequest
import com.team2.chitchat.data.repository.remote.response.users.PostRegisterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("api/users/register")
    suspend fun postRegisterUser(
        @Body registerUserRequest: RegisterUserRequest
    ): Response<PostRegisterResponse>
}