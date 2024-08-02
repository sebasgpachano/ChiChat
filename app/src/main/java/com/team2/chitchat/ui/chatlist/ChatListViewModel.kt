package com.team2.chitchat.ui.chatlist

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.team2.chitchat.data.domain.model.chats.ListChatsModel
import com.team2.chitchat.data.mapper.chats.ListChatsMapper
import com.team2.chitchat.data.repository.remote.response.BaseResponse
import com.team2.chitchat.data.session.DataUserSession
import com.team2.chitchat.data.domain.usecase.local.GetChatsDbUseCase
import com.team2.chitchat.data.domain.usecase.local.GetMessagesDbUseCase
import com.team2.chitchat.data.domain.usecase.local.GetUsersDbUseCase
import com.team2.chitchat.data.domain.usecase.local.UpdateChatViewUseCase
import com.team2.chitchat.data.domain.usecase.remote.DeleteChatUseCase
import com.team2.chitchat.ui.base.BaseViewModel
import com.team2.chitchat.ui.extensions.TAG
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatListViewModel @Inject constructor(
    private val dataUserSession: DataUserSession,
    private val getChatsDbUseCase: GetChatsDbUseCase,
    private val getMessagesDbUseCase: GetMessagesDbUseCase,
    private val getUsersDbUseCase: GetUsersDbUseCase,
    private val deleteChatUseCase: DeleteChatUseCase,
    private val updateChatViewUseCase: UpdateChatViewUseCase,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel() {
    private val chatsMutableSharedFlow: MutableSharedFlow<ArrayList<ListChatsModel>> =
        MutableSharedFlow()
    private val deleteChatMutableSharedFlow: MutableSharedFlow<Boolean> = MutableSharedFlow()
    val deleteChatSharedFlow: SharedFlow<Boolean> = deleteChatMutableSharedFlow
    val chatsSharedFlow: SharedFlow<ArrayList<ListChatsModel>> = chatsMutableSharedFlow

    fun getChats() {
        viewModelScope.launch(dispatcher) {
            loadingMutableSharedFlow.emit(true)

            val chatsFlow = getChatsDbUseCase()
            val messagesFlow = getMessagesDbUseCase()
            val usersFlow = getUsersDbUseCase()

            combine(
                usersFlow,
                chatsFlow,
                messagesFlow
            ) { usersResponse, chatsResponse, messagesResponse ->
                val listUsers = when (usersResponse) {
                    is BaseResponse.Error -> {
                        errorMutableSharedFlow.emit(usersResponse.error)
                        ArrayList()
                    }

                    is BaseResponse.Success -> usersResponse.data
                }

                val listChats = when (chatsResponse) {
                    is BaseResponse.Error -> {
                        errorMutableSharedFlow.emit(chatsResponse.error)
                        ArrayList()
                    }

                    is BaseResponse.Success -> chatsResponse.data
                }

                val listMessages = when (messagesResponse) {
                    is BaseResponse.Error -> {
                        errorMutableSharedFlow.emit(messagesResponse.error)
                        ArrayList()
                    }

                    is BaseResponse.Success -> messagesResponse.data
                }
                Triple(listUsers, listChats, listMessages)
            }.collect { (users, chats, messages) ->
                loadingMutableSharedFlow.emit(false)
                val listChatsMapper =
                    ListChatsMapper(
                        dataUserSession.userId,
                        users,
                        chats,
                        messages
                    )
                chatsMutableSharedFlow.emit(listChatsMapper.getList())
                resetView(listChatsMapper.getList())
            }
        }
    }

    private fun resetView(chats: ArrayList<ListChatsModel>) {
        for (chat in chats) {
            if (!chat.view) {
                updateChatView(chat.id, true)
            }
        }
    }

    fun deleteChat(id: String) {
        viewModelScope.launch(dispatcher) {
            loadingMutableSharedFlow.emit(true)
            deleteChatUseCase(id).collect {
                when (it) {
                    is BaseResponse.Error -> {
                        loadingMutableSharedFlow.emit(false)
                        Log.d(TAG, "chats> Da error ${it.error.errorCode}")
                        errorMutableSharedFlow.emit(it.error)
                    }

                    is BaseResponse.Success -> {
                        loadingMutableSharedFlow.emit(false)
                        Log.d(TAG, "chats> Delete chat viewModel${it.data}")
                        deleteChatMutableSharedFlow.emit(it.data)
                    }
                }
            }
        }
    }

    fun updateChatView(id: String, view: Boolean) {
        viewModelScope.launch(dispatcher) {
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
                        deleteChatMutableSharedFlow.emit(it.data)
                    }
                }
            }
        }
    }
}