package com.team2.chitchat.ui.chatlist

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.team2.chitchat.data.domain.model.chats.ListChatsModel
import com.team2.chitchat.data.mapper.chats.ListChatsMapper
import com.team2.chitchat.data.repository.remote.response.BaseResponse
import com.team2.chitchat.data.usecase.local.GetChatsDbUseCase
import com.team2.chitchat.data.usecase.local.GetMessagesDbUseCase
import com.team2.chitchat.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatListViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getChatsDbUseCase: GetChatsDbUseCase,
    private val getMessagesDbUseCase: GetMessagesDbUseCase
) :
    BaseViewModel() {
    private val chatsMutableSharedFlow: MutableSharedFlow<ArrayList<ListChatsModel>> =
        MutableSharedFlow()
    val chatsSharedFlow: SharedFlow<ArrayList<ListChatsModel>> = chatsMutableSharedFlow

    fun getChats() {
        viewModelScope.launch {
            loadingMutableSharedFlow.emit(true)

            val chatsFlow = getChatsDbUseCase()
            val messagesFlow = getMessagesDbUseCase()

            combine(chatsFlow, messagesFlow) { chatsResponse, messagesResponse ->
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
                Pair(listChats, listMessages)
            }.collect { (chats, messages) ->
                loadingMutableSharedFlow.emit(false)
                val listChatsMapper = ListChatsMapper(chats, messages, context)
                chatsMutableSharedFlow.emit(listChatsMapper.getList())
            }
        }
    }
}