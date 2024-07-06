package com.team2.chitchat.ui.profile

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.team2.chitchat.data.domain.model.users.GetUserModel
import com.team2.chitchat.data.repository.remote.response.BaseResponse
import com.team2.chitchat.data.usecase.local.DeleteUserTableUseCase
import com.team2.chitchat.data.usecase.remote.GetProfileUseCase
import com.team2.chitchat.data.usecase.remote.PutLogOutUseCase
import com.team2.chitchat.ui.base.BaseViewModel
import com.team2.chitchat.ui.extensions.TAG
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getProfileUseCase: GetProfileUseCase,
    private val putLogOutUseCase: PutLogOutUseCase,
    private val deleteUserTableUseCase: DeleteUserTableUseCase
) : BaseViewModel() {
    private val deleteDbMutableSharedFlow = MutableSharedFlow<Boolean>()
    val deleteDbSharedFlow: SharedFlow<Boolean> = deleteDbMutableSharedFlow

    private val _getUserModelMutableStateFlow: MutableStateFlow<GetUserModel> =
        MutableStateFlow(GetUserModel())
    val getUserStateFlow: StateFlow<GetUserModel> = _getUserModelMutableStateFlow
    private val _putLogOutMutableStateFlow: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val putLogOutStateFlow: StateFlow<Boolean> = _putLogOutMutableStateFlow

    init {
        loadUserModel()
    }

    private fun loadUserModel() {
        viewModelScope.launch(Dispatchers.IO) {
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

        viewModelScope.launch(Dispatchers.IO) {
            loadingMutableSharedFlow.emit(true)
            putLogOutUseCase().collect { response ->
                loadingMutableSharedFlow.emit(false)
                when (response) {
                    is BaseResponse.Success -> {
                        Log.d(TAG, "l>  putLogOut Success: ${response.data}")
                        _putLogOutMutableStateFlow.value = response.data
                    }

                    is BaseResponse.Error -> {
                        Log.d(TAG, "l> Error: ${response.error.message}")
                        errorMutableSharedFlow.emit(response.error)
                    }
                }
            }
        }
    }

    fun deleteDb() {
        Log.d(
            TAG,
            "%> Borrando bases de datos..."
        )
        viewModelScope.launch(Dispatchers.IO) {
            loadingMutableSharedFlow.emit(true)
            deleteUserTable()
        }
    }

    private fun deleteUserTable() {
        Log.d(
            TAG,
            "%> Borrando contactos..."
        )
        viewModelScope.launch(Dispatchers.IO) {
            deleteUserTableUseCase().collect { response ->
                when (response) {
                    is BaseResponse.Success -> {
                        Log.d(
                            TAG,
                            "%> Contactos borrados: ${response.data}"
                        )
                        deleteDbMutableSharedFlow.emit(response.data)
                    }

                    is BaseResponse.Error -> {
                        errorMutableSharedFlow.emit(response.error)
                    }
                }
            }
        }
    }
}