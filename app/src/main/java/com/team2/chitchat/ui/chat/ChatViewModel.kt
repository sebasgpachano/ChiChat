package com.team2.chitchat.ui.chat

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.team2.chitchat.data.domain.model.chats.GetChatModel
import com.team2.chitchat.data.domain.model.error.ErrorModel
import com.team2.chitchat.data.domain.model.messages.GetMessagesModel
import com.team2.chitchat.data.mapper.chats.GetChatMapper
import com.team2.chitchat.data.mapper.messages.MessagesMapper
import com.team2.chitchat.data.repository.remote.request.messages.NewMessageRequest
import com.team2.chitchat.data.repository.remote.response.BaseResponse
import com.team2.chitchat.data.usecase.local.GetChatUseCase
import com.team2.chitchat.data.usecase.local.GetMessagesForChatUseCase
import com.team2.chitchat.data.usecase.local.UpdateMessageViewUseCase
import com.team2.chitchat.data.usecase.remote.PostNewMessageUseCase
import com.team2.chitchat.hilt.SimpleApplication
import com.team2.chitchat.ui.base.BaseViewModel
import com.team2.chitchat.ui.extensions.TAG
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
    private val updateMessageViewUseCase: UpdateMessageViewUseCase
) :
    BaseViewModel() {
    private val messagesMutableStateFlow: MutableStateFlow<List<GetMessagesModel>> =
        MutableStateFlow(
            emptyList()
        )
    val messagesStateFlow: StateFlow<List<GetMessagesModel>> = messagesMutableStateFlow
    private val chatMutableStateFlow: MutableStateFlow<GetChatModel> =
        MutableStateFlow(GetChatModel("", "", "", false))
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
                        resetView(messages)
                    }
                }
            }
        }
    }

    private fun resetView(messages: List<GetMessagesModel>) {
        for (message in messages) {
            if (!message.view) {
                updateChatView(message.id, true)
            }
        }
    }

    private fun updateChatView(id: String, view: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            loadingMutableSharedFlow.emit(true)
            updateMessageViewUseCase(id, view).collect {
                when (it) {
                    is BaseResponse.Error -> {
                        loadingMutableSharedFlow.emit(false)
                        Log.d(TAG, "chats> Update chat viewModel${it.error.message}")
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
            getChatUseCase(chatId).collect { response ->
                when (response) {
                    is BaseResponse.Error -> {
                        errorMutableSharedFlow.emit(response.error)
                        loadingMutableSharedFlow.emit(false)
                    }

                    is BaseResponse.Success -> {
                        loadingMutableSharedFlow.emit(false)
                        if (response.data != null) {
                            val chat = getChatMapper.getChat(response.data)
                            chatMutableStateFlow.value = chat
                        }
                    }
                }
            }
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