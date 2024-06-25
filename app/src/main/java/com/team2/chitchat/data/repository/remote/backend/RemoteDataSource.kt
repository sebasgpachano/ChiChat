package com.team2.chitchat.data.repository.remote.backend

import com.team2.chitchat.data.domain.model.chats.GetChatsModel
import com.team2.chitchat.data.domain.model.messages.GetMessagesModel
import com.team2.chitchat.data.domain.model.users.GetUserModel
import com.team2.chitchat.data.domain.model.users.PostRegisterModel
import com.team2.chitchat.data.mapper.chats.GetChatsMapper
import com.team2.chitchat.data.mapper.messages.GetMessagesMapper
import com.team2.chitchat.data.mapper.users.GetContactsListMapper
import com.team2.chitchat.data.mapper.users.PostRegisterMapper
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
) : BaseService(), DataSource {

    //RegisterUser
    override fun postRegisterUser(registerUserRequest: RegisterUserRequest): Flow<BaseResponse<PostRegisterModel>> =
        flow {
            val apiResult = callApiService.callPostRegisterUser(registerUserRequest)
            if (apiResult is BaseResponse.Success) {
                emit(BaseResponse.Success(PostRegisterMapper().fromResponse(apiResult.data)))
            } else if (apiResult is BaseResponse.Error) {
                emit(BaseResponse.Error(apiResult.error))
            }
        }

    //LoginUser
    override fun postLoginUser(loginUserRequest: LoginUserRequest): Flow<BaseResponse<Boolean>> =
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
    override fun getContactsList(): Flow<BaseResponse<ArrayList<GetUserModel>>> = flow {
        val apiResult = callApiService.callGetContactsList()
        if (apiResult is BaseResponse.Success) {
            emit(BaseResponse.Success(GetContactsListMapper().fromResponse(apiResult.data)))
        } else if (apiResult is BaseResponse.Error) {
            emit(BaseResponse.Error(apiResult.error))
        }
    }

    //Chats
    override fun getChats(): Flow<BaseResponse<ArrayList<GetChatsModel>>> = flow {
        val apiResult = callApiService.callGetChats()
        if (apiResult is BaseResponse.Success) {
            emit(BaseResponse.Success(GetChatsMapper().fromResponse(apiResult.data)))
        } else if (apiResult is BaseResponse.Error) {
            emit(BaseResponse.Error(apiResult.error))
        }
    }

    //Messages
    override fun getMessage(): Flow<BaseResponse<ArrayList<GetMessagesModel>>> = flow {
        val apiResult = callApiService.callGetMessages()
        if (apiResult is BaseResponse.Success) {
            emit(BaseResponse.Success(GetMessagesMapper().fromResponse(apiResult.data)))
        } else if (apiResult is BaseResponse.Error) {
            emit(BaseResponse.Error(apiResult.error))
        }
    }

}