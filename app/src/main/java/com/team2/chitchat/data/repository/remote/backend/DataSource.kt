package com.team2.chitchat.data.repository.remote.backend

import com.team2.chitchat.data.domain.model.chats.GetChatsModel
import com.team2.chitchat.data.domain.model.chats.PostNewChatModel
import com.team2.chitchat.data.domain.model.messages.GetMessagesModel
import com.team2.chitchat.data.domain.model.users.GetUserModel
import com.team2.chitchat.data.domain.model.users.PostRegisterModel
import com.team2.chitchat.data.repository.remote.request.chats.NewChatRequest
import com.team2.chitchat.data.repository.remote.request.users.LoginUserRequest
import com.team2.chitchat.data.repository.remote.request.users.RegisterUserRequest
import com.team2.chitchat.data.repository.remote.response.BaseResponse
import kotlinx.coroutines.flow.Flow

interface DataSource {
    //RegisterUser
    fun postRegisterUser(registerUserRequest: RegisterUserRequest): Flow<BaseResponse<PostRegisterModel>>

    //LoginUser
    fun postLoginUser(loginUserRequest: LoginUserRequest): Flow<BaseResponse<Boolean>>

    //ContactsList
    fun getContactsList(): Flow<BaseResponse<ArrayList<GetUserModel>>>

    //Chats
    fun getChats(): Flow<BaseResponse<ArrayList<GetChatsModel>>>
    fun postNewChat(newChatRequest: NewChatRequest): Flow<BaseResponse<PostNewChatModel>>

    //Message
    fun getMessage(): Flow<BaseResponse<ArrayList<GetMessagesModel>>>

    // Get Profile
    fun getProfile(): Flow<BaseResponse<GetUserModel>>

    //LogOut
    fun putLogOut(): Flow<BaseResponse<Boolean>>

}