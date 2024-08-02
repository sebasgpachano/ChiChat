package com.team2.chitchat.ui.login


import android.util.Log
import androidx.lifecycle.viewModelScope
import com.team2.chitchat.data.repository.remote.request.users.LoginUserRequest
import com.team2.chitchat.data.repository.remote.response.BaseResponse
import com.team2.chitchat.data.domain.usecase.remote.GetRefreshTokenUseCase
import com.team2.chitchat.data.domain.usecase.preferences.IsBiometricStateUseCase
import com.team2.chitchat.data.domain.usecase.preferences.PutBiometricStateUseCase
import com.team2.chitchat.data.domain.usecase.remote.PostLoginUseCase
import com.team2.chitchat.ui.base.BaseViewModel
import com.team2.chitchat.ui.extensions.TAG
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val postLoginUseCase: PostLoginUseCase,
    private val getRefreshTokenUseCase: GetRefreshTokenUseCase,
    private val isBiometricStateUseCase: IsBiometricStateUseCase,
    private val putBiometricStateUseCase: PutBiometricStateUseCase,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
): BaseViewModel() {
    // Response of Login
    private val loginMutableStateFlow: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val loginStateFlow: StateFlow<Boolean> = loginMutableStateFlow
    
    //AccessBiometric PREFERENCES
    private val accessBiometricMutableStateFlow: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val accessBiometricStateFlow: StateFlow<Boolean> = accessBiometricMutableStateFlow

    //Get Api RefreshToken
    private val getRefreshTokenMutableStateFlow: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val getRefreshTokenStateFlow = getRefreshTokenMutableStateFlow.asStateFlow()

    init {
        loadAccessBiometric()
    }

    fun doLogin(loginUserRequest: LoginUserRequest) {
        viewModelScope.launch(dispatcher) {
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

    // RefreshToken
    fun loaRefreshToken(startDataBase: () -> Unit) {
        viewModelScope.launch(dispatcher) {
            loadingMutableSharedFlow.emit(true)
            getRefreshTokenUseCase().collect { baseResponse->
                when(baseResponse) {
                    is BaseResponse.Error -> {
                        Log.d(this@LoginViewModel.TAG, "l> Error: ${baseResponse.error.message}")
                        loadingMutableSharedFlow.emit(false)
                        errorMutableSharedFlow.emit(baseResponse.error)
                    }
                    is BaseResponse.Success -> {
                        startDataBase()
                        getRefreshTokenMutableStateFlow.value = baseResponse.data
                    }
                }
            }
        }
    }
    //AccessBiometric
    private fun loadAccessBiometric() {
        viewModelScope.launch(dispatcher) {
            isBiometricStateUseCase().collect { baseResponse->
                when(baseResponse) {
                    is BaseResponse.Error -> {
                        Log.d(this@LoginViewModel.TAG, "l> Error: ${baseResponse.error.message}")
                        errorMutableSharedFlow.emit(baseResponse.error)
                        }
                    is BaseResponse.Success -> {
                        accessBiometricMutableStateFlow.value = baseResponse.data
                    }
                    }
                }
            }
    }
    fun saveAccessBiometric(accessBiometric: Boolean) {
        viewModelScope.launch(dispatcher) {
            putBiometricStateUseCase(accessBiometric)
        }
    }

}