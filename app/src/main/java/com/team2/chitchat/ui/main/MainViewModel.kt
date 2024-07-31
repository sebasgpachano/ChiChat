package com.team2.chitchat.ui.main

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.team2.chitchat.data.repository.remote.response.BaseResponse
import com.team2.chitchat.data.domain.usecase.remote.PutOfflineUseCase
import com.team2.chitchat.data.domain.usecase.remote.PutOnlineUseCase
import com.team2.chitchat.ui.base.BaseViewModel
import com.team2.chitchat.ui.extensions.TAG
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val putOfflineUseCase: PutOfflineUseCase,
    private val putOnlineUseCase: PutOnlineUseCase,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel() {

    fun logOut() {
        viewModelScope.launch(dispatcher) {
            loadingMutableSharedFlow.emit(true)
            putOfflineUseCase().collect { response ->
                when (response) {
                    is BaseResponse.Success -> {
                        loadingMutableSharedFlow.emit(false)
                        Log.d(TAG, "State>  putLogOut Success: ${response.data}")
                    }

                    is BaseResponse.Error -> {
                        loadingMutableSharedFlow.emit(false)
                        Log.d(TAG, "State> Error: ${response.error.message}")
                    }
                }
            }
        }
    }

    fun putOnline() {
        viewModelScope.launch(dispatcher) {
            loadingMutableSharedFlow.emit(true)
            putOnlineUseCase().collect { response ->
                when (response) {
                    is BaseResponse.Success -> {
                        loadingMutableSharedFlow.emit(false)
                        Log.d(TAG, "State> putOnline Success: ${response.data}")
                    }

                    is BaseResponse.Error -> {
                        loadingMutableSharedFlow.emit(false)
                        Log.d(TAG, "State> Error: ${response.error.message}")
                    }
                }
            }
        }
    }
}