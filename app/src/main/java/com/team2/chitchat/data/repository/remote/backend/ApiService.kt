package com.team2.chitchat.data.repository.remote.backend

import com.team2.chitchat.data.repository.remote.request.users.LoginUserRequest
import com.team2.chitchat.data.repository.remote.request.users.RegisterUserRequest
import com.team2.chitchat.data.repository.remote.response.chats.GetChatsResponse
import com.team2.chitchat.data.repository.remote.response.messages.GetMessagesResponse
import com.team2.chitchat.data.repository.remote.response.users.GetUserResponse
import com.team2.chitchat.data.repository.remote.response.users.PostLoginResponse
import com.team2.chitchat.data.repository.remote.response.users.PostRegisterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    //RegisterUser
    @POST("api/users/register")
    suspend fun postRegisterUser(
        @Body registerUserRequest: RegisterUserRequest
    ): Response<PostRegisterResponse>

    //LoginUser
    @POST("api/users/login")
    suspend fun postLoginUser(
        @Body loginUserRequest: LoginUserRequest
    ): Response<PostLoginResponse>

    //ContactsList
    @GET("api/users")
    suspend fun getContactsList(): Response<ArrayList<GetUserResponse>>

    //Chats
    @GET("api/chats")
    suspend fun getChats(): Response<ArrayList<GetChatsResponse>>

    //Message
    @GET("api/messages")
    suspend fun getMessages(): Response<ArrayList<GetMessagesResponse>>
}