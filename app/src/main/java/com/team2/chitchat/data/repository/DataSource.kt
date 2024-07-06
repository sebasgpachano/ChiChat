package com.team2.chitchat.data.repository

import com.team2.chitchat.data.domain.model.chats.GetChatsModel
import com.team2.chitchat.data.domain.model.messages.GetMessagesModel
import com.team2.chitchat.data.domain.model.users.GetUserModel
import com.team2.chitchat.data.domain.model.users.PostRegisterModel
import com.team2.chitchat.data.repository.local.user.UserDB
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
    fun getContactsList(): Flow<BaseResponse<ArrayList<UserDB>>>

    //Chats
    fun getChats(): Flow<BaseResponse<ArrayList<GetChatsModel>>>

    //Message
    fun getMessage(): Flow<BaseResponse<ArrayList<GetMessagesModel>>>

    //Profile
    fun getProfile(): Flow<BaseResponse<GetUserModel>>

    //LogOut
    fun putLogOut(): Flow<BaseResponse<Boolean>>

    //User Database
    fun insertUsers(users: ArrayList<UserDB>): Flow<BaseResponse<Boolean>>
}