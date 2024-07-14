package com.team2.chitchat.ui.chat

import androidx.lifecycle.viewModelScope
import com.team2.chitchat.data.domain.model.messages.GetMessagesModel
import com.team2.chitchat.data.mapper.messages.MessagesMapper
import com.team2.chitchat.data.repository.remote.response.BaseResponse
import com.team2.chitchat.data.usecase.local.GetMessagesForChatUseCase
import com.team2.chitchat.data.usecase.remote.GetMessagesUseCase
import com.team2.chitchat.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val getMessagesForChatUseCase: GetMessagesForChatUseCase,
    private val messagesMapper: MessagesMapper
) :
    BaseViewModel() {
    private val messagesMutableStateFlow: MutableStateFlow<List<GetMessagesModel>> =
        MutableStateFlow(
            emptyList()
        )
    val messagesStateFlow: StateFlow<List<GetMessagesModel>> = messagesMutableStateFlow

    fun getMessagesForChat(chatId: String) {
        viewModelScope.launch {
            getMessagesForChatUseCase(chatId).collect {
                when (it) {
                    is BaseResponse.Error -> {
                        errorMutableSharedFlow.emit(it.error)
                    }

                    is BaseResponse.Success -> {
                        val messages = messagesMapper.getMessages(it.data)
                        messagesMutableStateFlow.value = messages
                    }
                }
            }
        }
    }
}