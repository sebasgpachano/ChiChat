package com.team2.chitchat.ui.main

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.team2.chitchat.data.repository.remote.response.BaseResponse
import com.team2.chitchat.data.usecase.local.SetUsersDatabaseUseCase
import com.team2.chitchat.data.usecase.remote.GetContactsUseCase
import com.team2.chitchat.hilt.SimpleApplication
import com.team2.chitchat.ui.base.BaseViewModel
import com.team2.chitchat.ui.extensions.TAG
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DbViewModel @Inject constructor(
    private val simpleApplication: SimpleApplication,
    private val getContactsUseCase: GetContactsUseCase,
    private val setUsersDatabaseUseCase: SetUsersDatabaseUseCase,
) : BaseViewModel() {
    private val initDbMutableSharedFlow = MutableSharedFlow<Boolean>()
    val initDbSharedFlow: SharedFlow<Boolean> = initDbMutableSharedFlow

    fun startDataBase() {
        val idUser = simpleApplication.getUserID()
        viewModelScope.launch(Dispatchers.IO) {
            loadingMutableSharedFlow.emit(true)
            startContact()
        }
    }

    private fun startContact() {
        viewModelScope.launch(Dispatchers.IO) {
            getContactsUseCase().collect { getContacts ->
                when (getContacts) {
                    is BaseResponse.Error -> {
                        errorMutableSharedFlow.emit(getContacts.error)
                    }

                    is BaseResponse.Success -> {
                        setUsersDatabaseUseCase(getContacts.data).collect { setContact ->
                            when (setContact) {
                                is BaseResponse.Error -> {
                                    errorMutableSharedFlow.emit(setContact.error)
                                }

                                is BaseResponse.Success -> {
                                    Log.d(
                                        TAG,
                                        "%> Respuesta de cargar contactos en DB -> ${setContact.data}"
                                    )
                                    initDbMutableSharedFlow.emit(setContact.data)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}