package com.team2.chitchat.ui.contactslist

import androidx.lifecycle.viewModelScope
import com.team2.chitchat.data.repository.local.user.UserDB
import com.team2.chitchat.data.repository.remote.response.BaseResponse
import com.team2.chitchat.data.usecase.remote.GetContactsUseCase
import com.team2.chitchat.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactsListViewModel @Inject constructor(private val getContactsUseCase: GetContactsUseCase) :
    BaseViewModel() {
    private val contactsMutableSharedFlow: MutableSharedFlow<ArrayList<UserDB>> =
        MutableSharedFlow()
    val contactsSharedFlow: SharedFlow<ArrayList<UserDB>> = contactsMutableSharedFlow

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
                        contactsMutableSharedFlow.emit(it.data)
                    }
                }
            }
        }
    }
}