package com.team2.chitchat.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team2.chitchat.data.domain.model.error.ErrorModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel : ViewModel() {
    protected val loadingMutableSharedFlow = MutableSharedFlow<Boolean>(replay = 0)
    val loadingFlow: SharedFlow<Boolean> = loadingMutableSharedFlow

    protected val errorMutableSharedFlow = MutableSharedFlow<ErrorModel>(replay = 0)
    val errorFlow: SharedFlow<ErrorModel> = errorMutableSharedFlow

    fun resetError() {
        viewModelScope.launch {
            errorMutableSharedFlow.emit(ErrorModel())
        }

    }
}