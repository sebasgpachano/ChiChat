package com.team2.chitchat.ui.contactslist

import androidx.lifecycle.viewModelScope
import com.team2.chitchat.data.domain.model.chats.PostNewChatModel
import com.team2.chitchat.data.domain.model.users.GetUserModel
import com.team2.chitchat.data.repository.remote.request.chats.NewChatRequest
import com.team2.chitchat.data.repository.remote.response.BaseResponse
import com.team2.chitchat.data.usecase.GetContactsUseCase
import com.team2.chitchat.data.usecase.PostNewChatUseCase
import com.team2.chitchat.hilt.SimpleApplication
import com.team2.chitchat.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactsListViewModel @Inject constructor(
    private val simpleApplication: SimpleApplication,
    private val getContactsUseCase: GetContactsUseCase,
    private val postNewChatUseCase: PostNewChatUseCase
) : BaseViewModel() {
    private val contactsMutableSharedFlow: MutableSharedFlow<ArrayList<GetUserModel>> =
        MutableSharedFlow()
    private val newChatsMutableSharedFlow: MutableSharedFlow<PostNewChatModel> = MutableSharedFlow()
    val newChatSharedFlow: SharedFlow<PostNewChatModel> = newChatsMutableSharedFlow
    val contactsSharedFlow: SharedFlow<ArrayList<GetUserModel>> = contactsMutableSharedFlow

    fun getContactsList() {
        viewModelScope.launch(Dispatchers.IO) {
            loadingMutableSharedFlow.emit(true)
            getContactsUseCase().collect {
                when (it) {
                    is BaseResponse.Error -> {
                        loadingMutableSharedFlow.emit(false)
                        errorMutableSharedFlow.emit(it.error)
                    }

                    is BaseResponse.Success -> {
                        loadingMutableSharedFlow.emit(false)
                        contactsMutableSharedFlow.emit(ArrayList(it.data.sortedBy { user -> user.nick }))
                    }
                }
            }
        }
    }

    fun postNewChat(target: String) {
        viewModelScope.launch(Dispatchers.IO) {
            loadingMutableSharedFlow.emit(true)
            val newChat = NewChatRequest(simpleApplication.getUserID(), target)
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