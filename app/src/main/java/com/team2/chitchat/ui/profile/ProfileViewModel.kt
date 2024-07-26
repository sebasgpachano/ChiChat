package com.team2.chitchat.ui.profile

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.team2.chitchat.data.domain.model.users.GetUserModel
import com.team2.chitchat.data.repository.remote.response.BaseResponse
import com.team2.chitchat.data.usecase.local.DeleteChatTableUseCase
import com.team2.chitchat.data.usecase.local.DeleteMessageTableUseCase
import com.team2.chitchat.data.usecase.local.DeleteUserTableUseCase
import com.team2.chitchat.data.usecase.preferences.ClearPreferencesUseCase
import com.team2.chitchat.data.usecase.preferences.IsBiometricStateUseCase
import com.team2.chitchat.data.usecase.preferences.LoadProfilePictureUseCase
import com.team2.chitchat.data.usecase.preferences.PutBiometricStateUseCase
import com.team2.chitchat.data.usecase.remote.GetProfileUseCase
import com.team2.chitchat.data.usecase.remote.PutLogOutUseCase
import com.team2.chitchat.ui.base.BaseViewModel
import com.team2.chitchat.ui.extensions.TAG
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getProfileUseCase: GetProfileUseCase,
    private val putLogOutUseCase: PutLogOutUseCase,
    private val deleteUserTableUseCase: DeleteUserTableUseCase,
    private val deleteChatTableUseCase: DeleteChatTableUseCase,
    private val deleteMessageTableUseCase: DeleteMessageTableUseCase,
    private val loadProfilePictureUseCase: LoadProfilePictureUseCase,
    private val isBiometricStateUseCase: IsBiometricStateUseCase,
    private val putBiometricStateUseCase: PutBiometricStateUseCase,
    private val clearPreferencesUseCase: ClearPreferencesUseCase,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel() {
    private val deleteDbMutableSharedFlow = MutableSharedFlow<Boolean>()
    val deleteDbSharedFlow: SharedFlow<Boolean> = deleteDbMutableSharedFlow

    private val _getUserModelMutableStateFlow: MutableStateFlow<GetUserModel> =
        MutableStateFlow(GetUserModel())
    val getUserStateFlow: StateFlow<GetUserModel> = _getUserModelMutableStateFlow
    private val _putLogOutMutableStateFlow: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val putLogOutStateFlow: StateFlow<Boolean> = _putLogOutMutableStateFlow

    private val profilePictureMutableStateFlow: MutableStateFlow<Bitmap?> = MutableStateFlow(null)
    val profilePictureStateFlow: StateFlow<Bitmap?> = profilePictureMutableStateFlow

    //AccessBiometric PREFERENCES
    private val accessBiometricMutableStateFlow: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val accessBiometricStateFlow: StateFlow<Boolean> = accessBiometricMutableStateFlow

    init {
        loadUserModel()
        loadPicture()
        loadAccessBiometric()
    }

    private fun loadUserModel() {
        viewModelScope.launch(dispatcher) {
            getProfileUseCase().collect { response ->

                when (response) {
                    is BaseResponse.Success -> {
                        _getUserModelMutableStateFlow.value = response.data
                    }

                    is BaseResponse.Error -> {
                        Log.d(TAG, "l> Error: ${response.error.message}")
                        errorMutableSharedFlow.emit(response.error)
                    }
                }

            }
        }
    }

    fun putLogOut() {

        viewModelScope.launch(dispatcher) {
            loadingMutableSharedFlow.emit(true)
            putLogOutUseCase().collect { response ->
                loadingMutableSharedFlow.emit(false)
                when (response) {
                    is BaseResponse.Success -> {
                        Log.d(TAG, "l>  putLogOut Success: ${response.data}")
                        deletePreference()
                        _putLogOutMutableStateFlow.value = response.data
                    }

                    is BaseResponse.Error -> {
                        Log.d(TAG, "l> Error: ${response.error.message}")
                        _putLogOutMutableStateFlow.value = true
                        errorMutableSharedFlow.emit(response.error)
                    }
                }
            }
        }
    }

    //DataBase
    fun deleteDb() {
        Log.d(
            TAG,
            "%> Delete DB..."
        )
        viewModelScope.launch {
            loadingMutableSharedFlow.emit(true)
            val deleteUsers = deleteUserTable()
            val deleteChats = deleteChatTable()
            val deleteMessages = deleteMessageTable()
            if (deleteUsers && deleteChats && deleteMessages) {
                loadingMutableSharedFlow.emit(false)
                deleteDbMutableSharedFlow.emit(true)
            }
        }
    }

    private suspend fun deleteUserTable(): Boolean {
        Log.d(TAG, "%> Delete contacts...")
        return withContext(dispatcher) {
            var response = false
            deleteUserTableUseCase().collect {
                response = when (it) {
                    is BaseResponse.Error -> false
                    is BaseResponse.Success -> it.data
                }
            }
            response
        }
    }

    private suspend fun deleteChatTable(): Boolean {
        Log.d(TAG, "%> Delete chats...")
        return withContext(dispatcher) {
            var response = false
            deleteChatTableUseCase().collect {
                response = when (it) {
                    is BaseResponse.Error -> false
                    is BaseResponse.Success -> it.data
                }
            }
            response
        }
    }

    private suspend fun deleteMessageTable(): Boolean {
        Log.d(TAG, "%> Delete messages...")
        return withContext(dispatcher) {
            var response = false
            deleteMessageTableUseCase().collect {
                response = when (it) {
                    is BaseResponse.Error -> false
                    is BaseResponse.Success -> it.data
                }
            }
            response
        }
    }

    private fun loadPicture() {
        viewModelScope.launch {
            profilePictureMutableStateFlow.value = loadProfilePictureUseCase()
        }
    }

    //Preferences
    private fun deletePreference() {
        clearPreferencesUseCase()
    }

    //AccessBiometric
    private fun loadAccessBiometric() {
        viewModelScope.launch(dispatcher) {
            isBiometricStateUseCase().collect { baseResponse ->
                when (baseResponse) {
                    is BaseResponse.Error -> {
                        Log.d(this@ProfileViewModel.TAG, "l> Error: ${baseResponse.error.message}")
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
            loadAccessBiometric()
        }

    }
}