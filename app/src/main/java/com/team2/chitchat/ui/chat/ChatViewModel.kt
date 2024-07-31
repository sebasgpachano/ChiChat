package com.team2.chitchat.ui.chat

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.team2.chitchat.data.domain.model.chats.GetChatModel
import com.team2.chitchat.data.domain.model.messages.GetMessagesModel
import com.team2.chitchat.data.mapper.chats.GetChatMapper
import com.team2.chitchat.data.mapper.messages.MessagesMapper
import com.team2.chitchat.data.repository.remote.request.messages.NewMessageRequest
import com.team2.chitchat.data.repository.remote.response.BaseResponse
import com.team2.chitchat.data.session.DataUserSession
import com.team2.chitchat.data.domain.usecase.local.GetChatUseCase
import com.team2.chitchat.data.domain.usecase.local.GetMessagesForChatUseCase
import com.team2.chitchat.data.domain.usecase.local.GetUsersDbUseCase
import com.team2.chitchat.data.domain.usecase.local.UpdateChatViewUseCase
import com.team2.chitchat.data.domain.usecase.local.UpdateMessageViewUseCase
import com.team2.chitchat.data.domain.usecase.remote.PostNewMessageUseCase
import com.team2.chitchat.ui.base.BaseViewModel
import com.team2.chitchat.ui.extensions.TAG
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val getMessagesForChatUseCase: GetMessagesForChatUseCase,
    private val messagesMapper: MessagesMapper,
    private val dataUserSession: DataUserSession,
    private val postNewMessageUseCase: PostNewMessageUseCase,
    private val getChatUseCase: GetChatUseCase,
    private val getChatMapper: GetChatMapper,
    private val updateMessageViewUseCase: UpdateMessageViewUseCase,
    private val updateChatViewUseCase: UpdateChatViewUseCase,
    private val getUsersDbUseCase: GetUsersDbUseCase,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) :
    BaseViewModel() {
    private val messagesMutableStateFlow: MutableStateFlow<List<GetMessagesModel>> =
        MutableStateFlow(
            emptyList()
        )
    val messagesStateFlow: StateFlow<List<GetMessagesModel>> = messagesMutableStateFlow
    private val chatMutableStateFlow: MutableStateFlow<GetChatModel> =
        MutableStateFlow(GetChatModel("", "", "", online = false, false))
    val chatStateFlow: StateFlow<GetChatModel> = chatMutableStateFlow

    fun getMessagesForChat(chatId: String) {
        viewModelScope.launch(dispatcher) {
            loadingMutableSharedFlow.emit(true)
            getMessagesForChatUseCase(chatId).collect {
                when (it) {
                    is BaseResponse.Error -> {
                        errorMutableSharedFlow.emit(it.error)
                        loadingMutableSharedFlow.emit(false)
                    }

                    is BaseResponse.Success -> {
                        loadingMutableSharedFlow.emit(false)
                        val messages = messagesMapper.getMessages(it.data)
                        messagesMutableStateFlow.value = messages
                        resetMessageView(messages)
                    }
                }
            }
        }
    }

    private fun resetMessageView(messages: List<GetMessagesModel>) {
        for (message in messages) {
            if (!message.view) {
                updateMessageView(message.id)
            }
        }
    }

    private fun updateMessageView(id: String) {
        viewModelScope.launch(dispatcher) {
            loadingMutableSharedFlow.emit(true)
            updateMessageViewUseCase(id, true).collect {
                when (it) {
                    is BaseResponse.Error -> {
                        loadingMutableSharedFlow.emit(false)
                        errorMutableSharedFlow.emit(it.error)
                    }

                    is BaseResponse.Success -> {
                        loadingMutableSharedFlow.emit(false)
                    }
                }
            }
        }
    }

    fun getChat(chatId: String) {
        viewModelScope.launch(dispatcher) {
            loadingMutableSharedFlow.emit(true)
            val chatFlow = getChatUseCase(chatId)
            val usersFlow = getUsersDbUseCase()

            combine(chatFlow, usersFlow) { chatResponse, usersResponse ->
                Pair(chatResponse, usersResponse)
            }.collect { (chatResponse, usersResponse) ->
                when (chatResponse) {
                    is BaseResponse.Error -> {
                        errorMutableSharedFlow.emit(chatResponse.error)
                        loadingMutableSharedFlow.emit(false)
                    }

                    is BaseResponse.Success -> {
                        if (usersResponse is BaseResponse.Success) {
                            loadingMutableSharedFlow.emit(false)
                            if (chatResponse.data != null) {
                                val chat =
                                    getChatMapper.getChat(chatResponse.data, usersResponse.data)
                                chatMutableStateFlow.value = chat
                                resetChatView(chat)
                            }
                        } else if (usersResponse is BaseResponse.Error) {
                            errorMutableSharedFlow.emit(usersResponse.error)
                            loadingMutableSharedFlow.emit(false)
                        }
                    }
                }
            }
        }
    }

    private fun updateChatView(id: String) {
        viewModelScope.launch(dispatcher) {
            loadingMutableSharedFlow.emit(true)
            updateChatViewUseCase(id, true).collect {
                when (it) {
                    is BaseResponse.Error -> {
                        loadingMutableSharedFlow.emit(false)
                        Log.d(TAG, "chats> Update chat viewModel${it.error.message}")
                        errorMutableSharedFlow.emit(it.error)
                    }

                    is BaseResponse.Success -> {
                        loadingMutableSharedFlow.emit(false)
                        Log.d(TAG, "chats> Update chat viewModel${it.data}")
                    }
                }
            }
        }
    }

    private fun resetChatView(chat: GetChatModel) {
        if (!chat.view) {
            updateChatView(chat.id)
        }
    }

    fun postNewMessage(message: String, chatId: String) {
        viewModelScope.launch(dispatcher) {
            val newMessage = NewMessageRequest(chatId, dataUserSession.userId, message)
            postNewMessageUseCase(newMessage).collect {
                when (it) {
                    is BaseResponse.Error -> {
                        errorMutableSharedFlow.emit(it.error)
                    }

                    is BaseResponse.Success -> {
                        getMessagesForChat(chatId)
                    }
                }
            }
        }
    }
}