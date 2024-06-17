package com.team2.chitchat.ui.registration

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.team2.chitchat.data.repository.remote.request.users.RegisterUserRequest
import com.team2.chitchat.data.repository.remote.response.BaseResponse
import com.team2.chitchat.data.usecase.PostRegisterUseCase
import com.team2.chitchat.ui.base.BaseViewModel
import com.team2.chitchat.utils.TAG
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(private val postRegisterUseCase: PostRegisterUseCase) :
    BaseViewModel() {

    private val _successFlow = MutableStateFlow(false)
    val successFlow: StateFlow<Boolean> = _successFlow

    fun postUser(user: String, password: String, nick: String) {
        viewModelScope.launch(Dispatchers.IO) {
            loadingMutableSharedFlow.emit(true)
            val newUser = RegisterUserRequest(user, password, nick, "", "and", "", false)
            postRegisterUseCase(newUser).collect {
                when (it) {
                    is BaseResponse.Error -> {
                        Log.d(this@RegistrationViewModel.TAG, "l> Error: ${it.error.message}")
                        loadingMutableSharedFlow.emit(false)
                        errorMutableSharedFlow.emit(it.error)
                    }

                    is BaseResponse.Success -> {
                        loadingMutableSharedFlow.emit(false)
                        Log.d(this@RegistrationViewModel.TAG, "l> Success ${it.data.userModel}")
                        _successFlow.value = true
                    }
                }
            }
        }
    }
}