package com.team2.chitchat.data.repository.remote.backend

import com.team2.chitchat.data.repository.remote.request.chats.NewChatRequest
import com.team2.chitchat.data.repository.remote.request.messages.NewMessageRequest
import com.team2.chitchat.data.repository.remote.request.users.LoginUserRequest
import com.team2.chitchat.data.repository.remote.request.users.RegisterUserRequest
import com.team2.chitchat.data.repository.remote.response.chats.DeleteResponse
import com.team2.chitchat.data.repository.remote.response.chats.GetChatsResponse
import com.team2.chitchat.data.repository.remote.response.chats.PostNewChatResponse
import com.team2.chitchat.data.repository.remote.response.messages.GetMessagesResponse
import com.team2.chitchat.data.repository.remote.response.messages.PostNewMessageResponse
import com.team2.chitchat.data.repository.remote.response.users.GetUserResponse
import com.team2.chitchat.data.repository.remote.response.users.PostLoginResponse
import com.team2.chitchat.data.repository.remote.response.users.PostRegisterResponse
import com.team2.chitchat.data.repository.remote.response.users.PutStateResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

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

    //Access token with refresh token for biometric
    @GET("api/users/biometric")
    suspend fun getAccessToken(
        @Header("Authorization") refreshToken: String
    ): Response<PostLoginResponse>

    // Log Out
    @POST("api/users/logout")
    suspend fun putLogOut(): Response<PutStateResponse>

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

    @DELETE("api/chats/{id}")
    suspend fun deleteChat(
        @Path("id") id: String
    ): Response<DeleteResponse>

    //Message
    @GET("api/messages/")
    suspend fun getMessages(): Response<ArrayList<GetMessagesResponse>>

    @POST("api/messages/new")
    suspend fun postNewMessage(
        @Body newMessageRequest: NewMessageRequest
    ): Response<PostNewMessageResponse>

    //Profile
    @GET("api/users/profile")
    suspend fun getProfile(

    ): Response<GetUserResponse>

    //State
    @PUT("api/users/online/true")
    suspend fun putOnline(): Response<PutStateResponse>
}