package com.team2.chitchat.ui.login

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.team2.chitchat.data.repository.remote.request.users.LoginUserRequest
import com.team2.chitchat.data.repository.remote.response.BaseResponse
import com.team2.chitchat.data.usecase.PostLoginUseCase
import com.team2.chitchat.ui.base.BaseViewModel
import com.team2.chitchat.ui.extensions.TAG
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val postLoginUseCase: PostLoginUseCase
): BaseViewModel(

) {

    private val loginMutableStateFlow: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val loginStateFlow: StateFlow<Boolean> = loginMutableStateFlow

    fun getAuthenticationUser(loginUserRequest: LoginUserRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            loadingMutableSharedFlow.emit(true)
            postLoginUseCase(loginUserRequest).collect {baseResponse ->
                when(baseResponse) {
                    is BaseResponse.Error -> {
                        Log.d(this@LoginViewModel.TAG, "l> Error: ${baseResponse.error.message}")
                        loadingMutableSharedFlow.emit(false)
                        errorMutableSharedFlow.emit(baseResponse.error)
                    }
                    is BaseResponse.Success -> {
                        loadingMutableSharedFlow.emit(false)
                        loginMutableStateFlow.value = baseResponse.data
                    }
                }


            }
        }

    }
}