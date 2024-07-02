package com.team2.chitchat.data.repository.remote.backend

import android.util.Log
import com.google.gson.Gson
import com.team2.chitchat.BuildConfig
import com.team2.chitchat.data.domain.model.error.ErrorModel
import com.team2.chitchat.data.repository.remote.response.BaseResponse
import com.team2.chitchat.data.repository.remote.response.error.ErrorResponse
import com.team2.chitchat.ui.extensions.TAG
import retrofit2.Response

abstract class BaseService {
    companion object {
        const val ERROR_USER_EXIST = 400
        const val ERROR_USER_NOT_FOUND = 404
        const val ERROR_UNAUTHORIZED = 401
        const val ERROR_PASSWORD_INCORRECT = 402
        const val ERROR_FORBIDDEN = 403
        const val ERROR_INTERNAL_SERVER = 500
    }
    suspend fun <T : Any> apiCall(call: suspend () -> Response<T>): BaseResponse<T> {
        val response: Response<T>
        try {
            response = call.invoke()

            return if (!response.isSuccessful) {
                val errorResponse = mapErrorResponse(response)
                Log.e(TAG, "l> errorResponse: ${errorResponse.message}")
                BaseResponse.Error(errorResponse)
            } else {
                response.body()?.let { body ->
                    BaseResponse.Success(body)
                } ?: BaseResponse.Error(mapErrorResponse(response))
            }
        } catch (throwable: Throwable) {
            Log.e(TAG, "l> throwable: ${throwable.message}")
            throwable.printStackTrace()
            return BaseResponse.Error(mapErrorResponse(throwable))
        }
    }

    private fun <T> mapErrorResponse(response: Response<T>): ErrorModel {
        val errorBody = response.errorBody()?.string()
        val errorData = try {
            val parsedData = Gson().fromJson(errorBody, ErrorResponse::class.java)

            when (response.code()) {
                400 -> {
                    parsedData.errorCode = ERROR_USER_EXIST.toString()
                    parsedData.error = response.message()
                }
                401 -> {
                    parsedData.errorCode = ERROR_UNAUTHORIZED.toString()
                    parsedData.error = response.message()
                }
                402 -> {
                    parsedData.errorCode = ERROR_PASSWORD_INCORRECT.toString()
                    parsedData.error = response.message()
                }
                403 -> {
                    parsedData.errorCode = ERROR_FORBIDDEN.toString()
                    parsedData.error = response.message()
                }
                404 -> {
                    parsedData.errorCode = ERROR_USER_NOT_FOUND.toString()
                    parsedData.error = response.message()
                }
                500 -> {
                    parsedData.errorCode = ERROR_INTERNAL_SERVER.toString()
                    parsedData.error = response.message()
                }
            }
            parsedData
        } catch (exception: java.lang.Exception) {
            Log.e(TAG, "l> exception: ${exception.message}")
            exception.printStackTrace()
            null
        }
        return ErrorModel(
            errorData?.error ?: "",
            errorData?.errorCode ?: "0",
            errorData?.message ?: errorData?.errorRegister ?: ""
        )
    }

    private fun mapErrorResponse(throwable: Throwable): ErrorModel {
        return if (BuildConfig.DEBUG) {
            ErrorModel("UNKNOW", "UNKNOW", throwable.message ?: "UNKNOW")
        } else {
            ErrorModel(
                "Lo sentimos, estamos presentando problemas de conexión.",
                "0",
                "Vuelve a intentarlo más tarde."
            )
        }
    }
}