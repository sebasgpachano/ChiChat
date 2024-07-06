package com.team2.chitchat.ui.chat

import com.team2.chitchat.data.usecase.GetMessagesUseCase
import com.team2.chitchat.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(private val getMessagesUseCase: GetMessagesUseCase) :
    BaseViewModel() {

}