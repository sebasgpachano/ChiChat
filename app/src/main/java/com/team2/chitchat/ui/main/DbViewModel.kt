package com.team2.chitchat.ui.main

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.team2.chitchat.data.repository.local.chat.ChatDB
import com.team2.chitchat.data.repository.local.message.MessageDB
import com.team2.chitchat.data.repository.local.user.UserDB
import com.team2.chitchat.data.repository.remote.response.BaseResponse
import com.team2.chitchat.data.session.DataUserSession
import com.team2.chitchat.data.domain.usecase.local.SetChatsDatabaseUseCase
import com.team2.chitchat.data.domain.usecase.local.SetMessagesDatabaseUseCase
import com.team2.chitchat.data.domain.usecase.local.SetUsersDatabaseUseCase
import com.team2.chitchat.data.domain.usecase.remote.GetChatsUseCase
import com.team2.chitchat.data.domain.usecase.remote.GetContactsUseCase
import com.team2.chitchat.data.domain.usecase.remote.GetMessagesUseCase
import com.team2.chitchat.ui.base.BaseViewModel
import com.team2.chitchat.ui.extensions.TAG
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class DbViewModel @Inject constructor(
    private val dataUserSession: DataUserSession,
    private val getContactsUseCase: GetContactsUseCase,
    private val setUsersDatabaseUseCase: SetUsersDatabaseUseCase,
    private val getChatsUseCase: GetChatsUseCase,
    private val setChatsDatabaseUseCase: SetChatsDatabaseUseCase,
    private val getMessagesUseCase: GetMessagesUseCase,
    private val setMessagesDatabaseUseCase: SetMessagesDatabaseUseCase,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel() {
    private val initDbMutableSharedFlow = MutableSharedFlow<Boolean>()
    val initDbSharedFlow: SharedFlow<Boolean> = initDbMutableSharedFlow

    fun startDataBase() {
        viewModelScope.launch {
            val idUser = dataUserSession.userId
            Log.d(TAG, "%> Starting db for user: $idUser...")
            loadingMutableSharedFlow.emit(true)
            val usersAdd = startContact()
            val chatsAdd = startChats()
            val messagesAdd = startMessages()
            if (usersAdd && chatsAdd && messagesAdd) {
                loadingMutableSharedFlow.emit(false)
                initDbMutableSharedFlow.emit(true)
            }
        }
    }

    private suspend fun getContacts(): ArrayList<UserDB> {
        return withContext(dispatcher) {
            var listUser = ArrayList<UserDB>()
            getContactsUseCase().collect {
                listUser = when (it) {
                    is BaseResponse.Error -> {
                        errorMutableSharedFlow.emit(it.error)
                        ArrayList()
                    }

                    is BaseResponse.Success -> it.data
                }
            }
            listUser
        }
    }

    private suspend fun startContact(): Boolean {
        Log.d(TAG, "%> Starting contacts...")
        return withContext(dispatcher) {
            var response = false
            setUsersDatabaseUseCase(getContacts()).collect {
                response = when (it) {
                    is BaseResponse.Error -> false
                    is BaseResponse.Success -> it.data
                }
            }
            response
        }
    }

    private suspend fun getChats(): ArrayList<ChatDB> {
        return withContext(dispatcher) {
            var listChat = ArrayList<ChatDB>()
            getChatsUseCase().collect {
                listChat = when (it) {
                    is BaseResponse.Error -> {
                        errorMutableSharedFlow.emit(it.error)
                        ArrayList()
                    }

                    is BaseResponse.Success -> it.data
                }
            }
            listChat
        }
    }

    private suspend fun startChats(): Boolean {
        Log.d(TAG, "%> Starting chats...")
        return withContext(dispatcher) {
            var response = false
            setChatsDatabaseUseCase(getChats()).collect {
                response = when (it) {
                    is BaseResponse.Error -> false
                    is BaseResponse.Success -> it.data
                }
            }
            response
        }
    }

    private suspend fun getMessages(chats: ArrayList<ChatDB>): ArrayList<MessageDB> {
        return withContext(dispatcher) {
            var listAllMessage = ArrayList<MessageDB>()
            getMessagesUseCase().collect {
                listAllMessage = when (it) {
                    is BaseResponse.Error -> {
                        errorMutableSharedFlow.emit(it.error)
                        ArrayList()
                    }

                    is BaseResponse.Success -> it.data
                }
            }
            val chatIds = chats.map { it.id }.toSet()
            val listMessage = listAllMessage.filter { it.chatId in chatIds }
            for (message in listMessage) {
                message.notified = true
            }
            ArrayList(listMessage)
        }
    }

    private suspend fun startMessages(): Boolean {
        Log.d(TAG, "%> Starting messages...")
        return withContext(dispatcher) {
            var response = false
            val messages = getMessages(getChats())
            setMessagesDatabaseUseCase(messages).collect {
                response = when (it) {
                    is BaseResponse.Error -> false
                    is BaseResponse.Success -> it.data
                }
            }
            response
        }
    }
}