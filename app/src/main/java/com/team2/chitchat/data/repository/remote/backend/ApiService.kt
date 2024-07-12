package com.team2.chitchat.data.repository.remote.backend

import com.team2.chitchat.data.repository.remote.request.chats.NewChatRequest
import com.team2.chitchat.data.repository.remote.request.users.LoginUserRequest
import com.team2.chitchat.data.repository.remote.request.users.RegisterUserRequest
import com.team2.chitchat.data.repository.remote.response.LogOutResponse
import com.team2.chitchat.data.repository.remote.response.chats.GetChatsResponse
import com.team2.chitchat.data.repository.remote.response.chats.PostNewChatResponse
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

    // Log Out
    @POST("api/users/logout")
    suspend fun putLogOut(): Response<LogOutResponse>

    //ContactsList
    @GET("api/users")
    suspend fun getContactsList(): Response<ArrayList<GetUserResponse>>

    //Chats
    @GET("api/chats/view")
    suspend fun getChats(): Response<ArrayList<GetChatsResponse>>

    @POST("api/chats")
    suspend fun postNewChat(
        @Body newChatRequest: NewChatRequest
    ): Response<PostNewChatResponse>

    //Message
    @GET("api/messages/")
    suspend fun getMessages(): Response<ArrayList<GetMessagesResponse>>

    //Profile
    @GET("api/users/profile")
    suspend fun getProfile(

    ): Response<GetUserResponse>
}