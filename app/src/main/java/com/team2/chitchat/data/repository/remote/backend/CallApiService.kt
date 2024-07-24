package com.team2.chitchat.data.repository.remote.backend

import android.content.Context
import com.team2.chitchat.data.repository.remote.request.chats.NewChatRequest
import com.team2.chitchat.data.repository.remote.request.messages.NewMessageRequest
import com.team2.chitchat.data.repository.remote.request.users.LoginUserRequest
import com.team2.chitchat.data.repository.remote.request.users.RegisterUserRequest
import com.team2.chitchat.data.repository.remote.response.BaseResponse
import com.team2.chitchat.data.repository.remote.response.chats.DeleteResponse
import com.team2.chitchat.data.repository.remote.response.chats.GetChatsResponse
import com.team2.chitchat.data.repository.remote.response.chats.PostNewChatResponse
import com.team2.chitchat.data.repository.remote.response.messages.GetMessagesResponse
import com.team2.chitchat.data.repository.remote.response.messages.PostNewMessageResponse
import com.team2.chitchat.data.repository.remote.response.users.GetUserResponse
import com.team2.chitchat.data.repository.remote.response.users.PostLoginResponse
import com.team2.chitchat.data.repository.remote.response.users.PostRegisterResponse
import com.team2.chitchat.data.repository.remote.response.users.PutStateResponse
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CallApiService @Inject constructor(
    private val apiService: ApiService,
    @ApplicationContext private val context: Context
) : BaseService(context) {

    //RegisterUser
    suspend fun callPostRegisterUser(registerUserRequest: RegisterUserRequest): BaseResponse<PostRegisterResponse> {
        return apiCall { apiService.postRegisterUser(registerUserRequest) }
    }

    //LoginUser
    suspend fun callPostLoginUser(loginUserRequest: LoginUserRequest): BaseResponse<PostLoginResponse> {
        return apiCall { apiService.postLoginUser(loginUserRequest) }
    }

    //Refresh token with Biometric
    suspend fun callPostRefreshToken(refreshToken: String): BaseResponse<PostLoginResponse> {
        return apiCall { apiService.getAccessToken(refreshToken) }
    }

    //ContactsList
    suspend fun callGetContactsList(): BaseResponse<ArrayList<GetUserResponse>> {
        return apiCall { apiService.getContactsList() }
    }

    //Chats
    suspend fun callGetChats(): BaseResponse<ArrayList<GetChatsResponse>> {
        return apiCall { apiService.getChats() }
    }

    suspend fun callPostNewChat(newChatRequest: NewChatRequest): BaseResponse<PostNewChatResponse> {
        return apiCall { apiService.postNewChat(newChatRequest) }
    }

    suspend fun callDeleteChat(id: String): BaseResponse<DeleteResponse> {
        return apiCall { apiService.deleteChat(id) }
    }

    //Messages
    suspend fun callGetMessages(): BaseResponse<ArrayList<GetMessagesResponse>> {
        return apiCall { apiService.getMessages() }
    }

    suspend fun callPostNewMessage(newMessageRequest: NewMessageRequest): BaseResponse<PostNewMessageResponse> {
        return apiCall { apiService.postNewMessage(newMessageRequest) }
    }

    // Get Profile
    suspend fun callGetProfile(): BaseResponse<GetUserResponse> {
        return apiCall { apiService.getProfile() }
    }

    suspend fun callLogout(): BaseResponse<PutStateResponse> {
        return apiCall { apiService.putLogOut() }
    }

    //State
    suspend fun callPutOnline(): BaseResponse<PutStateResponse> {
        return apiCall { apiService.putOnline() }
    }
}