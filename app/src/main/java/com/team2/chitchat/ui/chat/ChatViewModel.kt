package com.team2.chitchat.ui.chat

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.team2.chitchat.data.domain.model.chats.GetChatModel
import com.team2.chitchat.data.domain.model.error.ErrorModel
import com.team2.chitchat.data.domain.model.messages.GetMessagesModel
import com.team2.chitchat.data.mapper.chats.GetChatMapper
import com.team2.chitchat.data.mapper.messages.MessagesMapper
import com.team2.chitchat.data.repository.local.user.UserDB
import com.team2.chitchat.data.repository.remote.request.messages.NewMessageRequest
import com.team2.chitchat.data.repository.remote.response.BaseResponse
import com.team2.chitchat.data.usecase.local.GetChatUseCase
import com.team2.chitchat.data.usecase.local.GetMessagesForChatUseCase
import com.team2.chitchat.data.usecase.local.GetUsersDbUseCase
import com.team2.chitchat.data.usecase.local.UpdateChatViewUseCase
import com.team2.chitchat.data.usecase.local.UpdateMessageViewUseCase
import com.team2.chitchat.data.usecase.remote.PostNewMessageUseCase
import com.team2.chitchat.hilt.SimpleApplication
import com.team2.chitchat.ui.base.BaseViewModel
import com.team2.chitchat.ui.extensions.TAG
import dagger.hilt.android.lifecycle.HiltViewModel
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
    private val simpleApplication: SimpleApplication,
    private val postNewMessageUseCase: PostNewMessageUseCase,
    private val getChatUseCase: GetChatUseCase,
    private val getChatMapper: GetChatMapper,
    private val updateMessageViewUseCase: UpdateMessageViewUseCase,
    private val updateChatViewUseCase: UpdateChatViewUseCase,
    private val getUsersDbUseCase: GetUsersDbUseCase
) :
    BaseViewModel() {
    private val messagesMutableStateFlow: MutableStateFlow<List<GetMessagesModel>> =
        MutableStateFlow(
            emptyList()
        )
    val messagesStateFlow: StateFlow<List<GetMessagesModel>> = messagesMutableStateFlow
    private val chatMutableStateFlow: MutableStateFlow<GetChatModel> =
        MutableStateFlow(GetChatModel("", "", "", false, false))
    val chatStateFlow: StateFlow<GetChatModel> = chatMutableStateFlow

    fun getMessagesForChat(chatId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            getMessagesForChatUseCase(chatId).collect {
                when (it) {
                    is BaseResponse.Error -> {
                        errorMutableSharedFlow.emit(it.error)
                    }

                    is BaseResponse.Success -> {
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
                updateMessageView(message.id, true)
            }
        }
    }

    private fun updateMessageView(id: String, view: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            loadingMutableSharedFlow.emit(true)
            updateMessageViewUseCase(id, view).collect {
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
        viewModelScope.launch(Dispatchers.IO) {
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

    private fun updateChatView(id: String, view: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            loadingMutableSharedFlow.emit(true)
            updateChatViewUseCase(id, view).collect {
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
            updateChatView(chat.id, true)
        }
    }

    fun postNewMessage(message: String, chatId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val newMessage = NewMessageRequest(chatId, simpleApplication.getUserID(), message)
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