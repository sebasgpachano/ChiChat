package com.team2.chitchat.data.repository.remote.response

import com.team2.chitchat.data.domain.model.error.ErrorModel

sealed class BaseResponse<T> {
    class Success<T>(val data: T) : BaseResponse<T>()
    class Error<T>(val error: ErrorModel) : BaseResponse<T>()
}