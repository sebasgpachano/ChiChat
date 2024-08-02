package com.team2.chitchat.ui.contactslist

import android.icu.lang.UCharacter.toLowerCase
import androidx.lifecycle.viewModelScope
import com.team2.chitchat.data.domain.model.chats.PostNewChatModel
import com.team2.chitchat.data.repository.local.user.UserDB
import com.team2.chitchat.data.repository.remote.request.chats.NewChatRequest
import com.team2.chitchat.data.repository.remote.response.BaseResponse
import com.team2.chitchat.data.session.DataUserSession
import com.team2.chitchat.data.domain.usecase.remote.GetContactsUseCase
import com.team2.chitchat.data.domain.usecase.remote.PostNewChatUseCase
import com.team2.chitchat.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactsListViewModel @Inject constructor(
    private val dataUserSession: DataUserSession,
    private val getContactsUseCase: GetContactsUseCase,
    private val postNewChatUseCase: PostNewChatUseCase,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel() {
    private val contactsMutableSharedFlow: MutableSharedFlow<ArrayList<UserDB>> =
        MutableSharedFlow()
    private val newChatsMutableSharedFlow: MutableSharedFlow<PostNewChatModel> = MutableSharedFlow()
    val newChatSharedFlow: SharedFlow<PostNewChatModel> = newChatsMutableSharedFlow
    val contactsSharedFlow: SharedFlow<ArrayList<UserDB>> = contactsMutableSharedFlow

    fun getContactsList() {
        viewModelScope.launch(dispatcher) {
            loadingMutableSharedFlow.emit(true)
            getContactsUseCase().collect {
                when (it) {
                    is BaseResponse.Error -> {
                        loadingMutableSharedFlow.emit(false)
                        errorMutableSharedFlow.emit(it.error)
                    }

                    is BaseResponse.Success -> {
                        loadingMutableSharedFlow.emit(false)
                        val filteredSortedList = it.data
                            .filterNot { user -> user.id == dataUserSession.userId }
                            .filterNot { user -> user.nick.isEmpty() }
                            .sortedBy { user -> toLowerCase(user.nick) }
                        contactsMutableSharedFlow.emit(ArrayList(filteredSortedList))
                    }
                }
            }
        }
    }

    fun postNewChat(target: String) {
        viewModelScope.launch(dispatcher) {
            loadingMutableSharedFlow.emit(true)
            val newChat = NewChatRequest(dataUserSession.userId, target)
            postNewChatUseCase(newChat).collect {
                when (it) {
                    is BaseResponse.Error -> {
                        loadingMutableSharedFlow.emit(false)
                        errorMutableSharedFlow.emit(it.error)
                    }

                    is BaseResponse.Success -> {
                        loadingMutableSharedFlow.emit(false)
                        newChatsMutableSharedFlow.emit(it.data)
                    }
                }
            }
        }
    }
}