package com.team2.chitchat.ui.login


import android.util.Log
import androidx.lifecycle.viewModelScope
import com.team2.chitchat.data.repository.crypto.BiometricCryptoManager
import com.team2.chitchat.data.repository.remote.request.users.LoginUserRequest
import com.team2.chitchat.data.repository.remote.response.BaseResponse
import com.team2.chitchat.data.usecase.preferences.AccessBiometricUseCase
import com.team2.chitchat.data.usecase.preferences.GetPasswordLoginUseCase
import com.team2.chitchat.data.usecase.preferences.PutAccessBiometricUseCase
import com.team2.chitchat.data.usecase.preferences.SavePasswordLoginUseCase
import com.team2.chitchat.data.usecase.remote.PostLoginUseCase
import com.team2.chitchat.ui.base.BaseViewModel
import com.team2.chitchat.ui.extensions.TAG
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.crypto.Cipher
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val postLoginUseCase: PostLoginUseCase,
    private val getPasswordLoginUseCase: GetPasswordLoginUseCase,
    private val savePasswordLoginUseCase: SavePasswordLoginUseCase,
    private val accessBiometricUseCase: AccessBiometricUseCase,
    private val putAccessBiometricUseCase: PutAccessBiometricUseCase,
    private val biometricCryptoManager: BiometricCryptoManager
) : BaseViewModel() {

    private val loginMutableStateFlow: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val loginStateFlow: StateFlow<Boolean> = loginMutableStateFlow

    //passwordLogin
    private val passwordLoginMutableStateFlow: MutableStateFlow<String> = MutableStateFlow("")
    val passwordLoginStateFlow: StateFlow<String> = passwordLoginMutableStateFlow

    //AccessBiometric
    private val accessBiometricMutableStateFlow: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val accessBiometricStateFlow: StateFlow<Boolean> = accessBiometricMutableStateFlow

    init {
        loadAccessBiometric()
    }

    fun doLogin(loginUserRequest: LoginUserRequest) {
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

    //passwordLogin
    fun getPasswordLogin(): String {
        val userPasswordLogin = getPasswordLoginUseCase()
        return userPasswordLogin
    }

    fun savePasswordLogin(cipher: Cipher, loginUserRequest: LoginUserRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            val encryptPasswordLogin = biometricCryptoManager.encrypt(cipher, loginUserRequest)
            Log.d(this@LoginViewModel.TAG, "savePasswordLogin: $encryptPasswordLogin")
            savePasswordLoginUseCase(encryptPasswordLogin)
        }
    }

    //AccessBiometric
    fun loadAccessBiometric() {
        viewModelScope.launch(Dispatchers.IO) {
            accessBiometricUseCase().collect { baseResponse ->
                when (baseResponse) {
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
        viewModelScope.launch(Dispatchers.IO) {
            putAccessBiometricUseCase(accessBiometric)
            loadAccessBiometric()
        }

    }

    fun getCipher(isEncrypt: Boolean): Cipher {
        return if (!isEncrypt) {
            biometricCryptoManager.decryptCipher(getPasswordLogin())
        } else {
            biometricCryptoManager.encryptedCipher()
        }
    }

    fun getLogin(cipher: Cipher): LoginUserRequest {
        val decryptPasswordLogin = biometricCryptoManager.decrypt(cipher, getPasswordLogin())
        Log.d(this@LoginViewModel.TAG, "decryptPasswordLogin: $decryptPasswordLogin")
        return decryptPasswordLogin
    }

}