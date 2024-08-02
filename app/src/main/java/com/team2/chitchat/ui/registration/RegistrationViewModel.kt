package com.team2.chitchat.ui.registration

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.team2.chitchat.data.repository.remote.request.users.RegisterUserRequest
import com.team2.chitchat.data.repository.remote.response.BaseResponse
import com.team2.chitchat.data.domain.usecase.preferences.SaveProfilePictureUseCase
import com.team2.chitchat.data.domain.usecase.remote.PostRegisterUseCase
import com.team2.chitchat.ui.base.BaseViewModel
import com.team2.chitchat.ui.extensions.TAG
import dagger.hilt.android.lifecycle.HiltViewModel
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val postRegisterUseCase: PostRegisterUseCase,
    private val saveProfilePictureUseCase: SaveProfilePictureUseCase,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) :
    BaseViewModel() {

    private val successSharedFlow = MutableSharedFlow<Boolean>()
    val successFlow: SharedFlow<Boolean> = successSharedFlow

    fun postUser(user: String, password: String, nick: String) {
        viewModelScope.launch(dispatcher) {
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
                        successSharedFlow.emit(true)
                    }
                }
            }
        }
    }

    fun saveProfilePicture(imageView: CircleImageView?) {
        viewModelScope.launch {
            saveProfilePictureUseCase(imageView)
        }
    }
}