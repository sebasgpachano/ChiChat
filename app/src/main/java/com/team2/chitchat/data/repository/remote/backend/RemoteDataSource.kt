package com.team2.chitchat.data.repository.remote.backend

import com.team2.chitchat.data.domain.model.chats.PostNewChatModel
import com.team2.chitchat.data.domain.model.users.GetUserModel
import com.team2.chitchat.data.domain.model.users.PostRegisterModel
import com.team2.chitchat.data.mapper.chats.GetChatsMapper
import com.team2.chitchat.data.mapper.chats.PostNewChatMapper
import com.team2.chitchat.data.mapper.messages.GetMessagesMapper
import com.team2.chitchat.data.mapper.users.GetContactsListMapper
import com.team2.chitchat.data.mapper.users.GetUserMapper
import com.team2.chitchat.data.mapper.users.PostRegisterMapper
import com.team2.chitchat.data.repository.local.chat.ChatDB
import com.team2.chitchat.data.repository.local.message.MessageDB
import com.team2.chitchat.data.repository.local.user.UserDB
import com.team2.chitchat.data.repository.remote.request.chats.NewChatRequest
import com.team2.chitchat.data.repository.remote.request.users.LoginUserRequest
import com.team2.chitchat.data.repository.remote.request.users.RegisterUserRequest
import com.team2.chitchat.data.repository.remote.response.BaseResponse
import com.team2.chitchat.hilt.SimpleApplication
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val callApiService: CallApiService,
    private val simpleApplication: SimpleApplication
) : BaseService() {

    //RegisterUser
    fun postRegisterUser(registerUserRequest: RegisterUserRequest): Flow<BaseResponse<PostRegisterModel>> =
        flow {
            val apiResult = callApiService.callPostRegisterUser(registerUserRequest)
            if (apiResult is BaseResponse.Success) {
                apiResult.data.let { response ->
                    simpleApplication.apply {
                        saveAuthToken(response.user?.token ?: "")
                        saveUserID(response.user?.id ?: "")
                    }
                }
                emit(BaseResponse.Success(PostRegisterMapper().fromResponse(apiResult.data)))
            } else if (apiResult is BaseResponse.Error) {
                emit(BaseResponse.Error(apiResult.error))
            }
        }

    //LoginUser
    fun postLoginUser(loginUserRequest: LoginUserRequest): Flow<BaseResponse<Boolean>> =
        flow {
            val apiResult = callApiService.callPostLoginUser(loginUserRequest)
            if (apiResult is BaseResponse.Success) {
                apiResult.data.let { response->
                    simpleApplication.apply {
                        saveAuthToken(response.token?:"")
                        saveUserID(response.user?.id?:"")
                    }
                }
                emit(BaseResponse.Success(true))
            } else if (apiResult is BaseResponse.Error) {
                emit(BaseResponse.Error(apiResult.error))
            }
        }

    //ContactsList
    fun getContactsList(): Flow<BaseResponse<ArrayList<UserDB>>> = flow {
        val apiResult = callApiService.callGetContactsList()
        if (apiResult is BaseResponse.Success) {
            emit(BaseResponse.Success(GetContactsListMapper().fromResponse(apiResult.data)))
        } else if (apiResult is BaseResponse.Error) {
            emit(BaseResponse.Error(apiResult.error))
        }
    }

    //Chats
    fun getChats(): Flow<BaseResponse<ArrayList<ChatDB>>> = flow {
        val apiResult = callApiService.callGetChats()
        if (apiResult is BaseResponse.Success) {
            emit(BaseResponse.Success(GetChatsMapper(simpleApplication).fromResponse(apiResult.data)))
        } else if (apiResult is BaseResponse.Error) {
            emit(BaseResponse.Error(apiResult.error))
        }
    }

    fun postNewChat(newChatRequest: NewChatRequest): Flow<BaseResponse<PostNewChatModel>> =
        flow {
            val apiResult = callApiService.callPostNewChat(newChatRequest)
            if (apiResult is BaseResponse.Success) {
                emit(BaseResponse.Success(PostNewChatMapper().fromResponse(apiResult.data)))
            } else if (apiResult is BaseResponse.Error) {
                emit(BaseResponse.Error(apiResult.error))
            }
        }

    //Messages
    fun getMessage(): Flow<BaseResponse<ArrayList<MessageDB>>> = flow {
        val apiResult = callApiService.callGetMessages()
        if (apiResult is BaseResponse.Success) {
            emit(BaseResponse.Success(GetMessagesMapper().fromResponse(apiResult.data)))
        } else if (apiResult is BaseResponse.Error) {
            emit(BaseResponse.Error(apiResult.error))
        }
    }

    //Profile
    fun getProfile(): Flow<BaseResponse<GetUserModel>> = flow {
        val apiResult = callApiService.callGetProfile()
        if (apiResult is BaseResponse.Success) {
            emit(BaseResponse.Success(GetUserMapper().fromResponse(apiResult.data)))
        } else if (apiResult is BaseResponse.Error) {
            emit(BaseResponse.Error(apiResult.error))
        }
    }

    //Log out
    fun putLogOut(): Flow<BaseResponse<Boolean>> = flow {
        val apiResult = callApiService.callLogout()
        if (apiResult is BaseResponse.Success) {
            simpleApplication.saveAuthToken("")
            emit(BaseResponse.Success(true))
        } else if (apiResult is BaseResponse.Error) {
            emit(BaseResponse.Error(apiResult.error))
        }
    }

}