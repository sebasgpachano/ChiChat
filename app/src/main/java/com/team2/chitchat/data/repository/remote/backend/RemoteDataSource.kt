package com.team2.chitchat.data.repository.remote.backend

import android.content.Context
import com.team2.chitchat.data.domain.model.chats.PostNewChatModel
import com.team2.chitchat.data.domain.model.messages.PostNewMessageModel
import com.team2.chitchat.data.domain.model.users.GetUserModel
import com.team2.chitchat.data.domain.model.users.PostRegisterModel
import com.team2.chitchat.data.mapper.chats.GetChatsMapper
import com.team2.chitchat.data.mapper.chats.PostNewChatMapper
import com.team2.chitchat.data.mapper.messages.GetMessagesMapper
import com.team2.chitchat.data.mapper.messages.PostNewMessageMapper
import com.team2.chitchat.data.mapper.users.GetContactsListMapper
import com.team2.chitchat.data.mapper.users.GetUserMapper
import com.team2.chitchat.data.mapper.users.PostRegisterMapper
import com.team2.chitchat.data.repository.crypto.BiometricCryptoManager
import com.team2.chitchat.data.repository.local.chat.ChatDB
import com.team2.chitchat.data.repository.local.message.MessageDB
import com.team2.chitchat.data.repository.local.user.UserDB
import com.team2.chitchat.data.repository.preferences.PreferencesDataSource
import com.team2.chitchat.data.repository.remote.request.chats.NewChatRequest
import com.team2.chitchat.data.repository.remote.request.messages.NewMessageRequest
import com.team2.chitchat.data.repository.remote.request.users.LoginUserRequest
import com.team2.chitchat.data.repository.remote.request.users.RegisterUserRequest
import com.team2.chitchat.data.repository.remote.response.BaseResponse
import com.team2.chitchat.data.session.DataUserSession
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val callApiService: CallApiService,
    private val dataUserSession: DataUserSession,
    private val preferencesDataSource: PreferencesDataSource,
    @ApplicationContext private val context: Context
) : BaseService(context) {

    //RegisterUser
    fun postRegisterUser(registerUserRequest: RegisterUserRequest): Flow<BaseResponse<PostRegisterModel>> =
        flow {
            val apiResult = callApiService.callPostRegisterUser(registerUserRequest)
            if (apiResult is BaseResponse.Success) {
                apiResult.data.let { response ->
                    dataUserSession.apply {
                        tokenIb = response.user?.token ?: ""
                        userId = response.user?.id ?: ""
                    }
                    preferencesDataSource.apply {
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
                apiResult.data.let { response ->
                    dataUserSession.apply {
                        tokenIb = response.token ?: ""
                        userId = response.user?.id ?: ""
                    }
                    preferencesDataSource.apply {
                        saveProfilePicture(null)
                        saveAuthToken(response.token ?: "")
                        saveUserID(response.user?.id ?: "")
                    }
                    emit(BaseResponse.Success(true))
                }

            } else if (apiResult is BaseResponse.Error) {
                emit(BaseResponse.Error(apiResult.error))
            }
        }

    //Access with Biometric
    fun postRefreshToken(biometricCryptoManager: BiometricCryptoManager): Flow<BaseResponse<Boolean>> =
        flow {
            val apiResult = callApiService.callPostRefreshToken()
            if (apiResult is BaseResponse.Success) {
                apiResult.data.let { response ->
                    response.token?.let { mToken->
                        dataUserSession.apply {
                            tokenIb = mToken
                            userId = response.user?.id ?: ""
                        }
                        biometricCryptoManager.encrypt(mToken)
                        preferencesDataSource.apply {
                            saveUserID(response.user?.id ?: "")
                        }
                    }
                    emit(BaseResponse.Success(true))
                }

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
            emit(BaseResponse.Success(GetChatsMapper(dataUserSession.userId).fromResponse(apiResult.data)))
        } else if (apiResult is BaseResponse.Error) {
            emit(BaseResponse.Error(apiResult.error))
        }
    }

    fun postNewChat(newChatRequest: NewChatRequest): Flow<BaseResponse<PostNewChatModel>> = flow {
        val apiResult = callApiService.callPostNewChat(newChatRequest)
        if (apiResult is BaseResponse.Success) {
            emit(BaseResponse.Success(PostNewChatMapper().fromResponse(apiResult.data)))
        } else if (apiResult is BaseResponse.Error) {
            emit(BaseResponse.Error(apiResult.error))
        }
    }

    fun deleteChat(id: String): Flow<BaseResponse<Boolean>> = flow {
        val apiResult = callApiService.callDeleteChat(id)
        if (apiResult is BaseResponse.Success) {

            emit(BaseResponse.Success(true))
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

    fun postNewMessage(newMessageRequest: NewMessageRequest): Flow<BaseResponse<PostNewMessageModel>> =
        flow {
            val apiResult = callApiService.callPostNewMessage(newMessageRequest)
            if (apiResult is BaseResponse.Success) {
                emit(BaseResponse.Success(PostNewMessageMapper().fromResponse(apiResult.data)))
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
        dataUserSession.tokenIb = ""
        dataUserSession.userId = ""
        preferencesDataSource.apply {
            saveAuthToken("")
            saveUserID("")
        }
        preferencesDataSource.saveAccessBiometric(false)
        if (apiResult is BaseResponse.Success) {
            emit(BaseResponse.Success(true))
        } else if (apiResult is BaseResponse.Error) {
            emit(BaseResponse.Error(apiResult.error))
        }
    }

    //State
    fun putOnline(): Flow<BaseResponse<Boolean>> = flow {
        val apiResult = callApiService.callPutOnline()
        if (apiResult is BaseResponse.Success) {
            emit(BaseResponse.Success(true))
        } else if (apiResult is BaseResponse.Error) {
            emit(BaseResponse.Error(apiResult.error))
        }
    }

    fun putOffline(): Flow<BaseResponse<Boolean>> = flow {
        val apiResult = callApiService.callPutOffline()
        if (apiResult is BaseResponse.Success) {
            emit(BaseResponse.Success(true))
        } else if (apiResult is BaseResponse.Error) {
            emit(BaseResponse.Error(apiResult.error))
        }
    }

}