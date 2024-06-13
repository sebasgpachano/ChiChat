package com.team2.chitchat.ui.contactslist

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.team2.chitchat.data.domain.model.users.GetUserModel
import com.team2.chitchat.data.repository.remote.response.BaseResponse
import com.team2.chitchat.data.usecase.GetContactsUseCase
import com.team2.chitchat.ui.base.BaseViewModel
import com.team2.chitchat.utils.TAG
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactsListViewModel @Inject constructor(private val getContactsUseCase: GetContactsUseCase) :
    BaseViewModel() {
    private val contactsMutableSharedFlow: MutableSharedFlow<ArrayList<GetUserModel>> =
        MutableSharedFlow()
    val contactsSharedFlow: SharedFlow<ArrayList<GetUserModel>> = contactsMutableSharedFlow

    fun getContactsList() {
        viewModelScope.launch(Dispatchers.IO) {
            loadingMutableSharedFlow.emit(true)
            getContactsUseCase().collect {
                when (it) {
                    is BaseResponse.Error -> {
                        Log.d(TAG, "%> Error: ${it.error.message}")
                        loadingMutableSharedFlow.emit(false)
                        errorMutableSharedFlow.emit(it.error)
                    }

                    is BaseResponse.Success -> {
                        loadingMutableSharedFlow.emit(false)
                        Log.d(TAG, "%> Success ${it.data.size}")
                        contactsMutableSharedFlow.emit(it.data)
                    }
                }
            }
        }
    }
}