package com.team2.chitchat.data.domain.model.error

import com.team2.chitchat.data.domain.model.BaseModel

class ErrorModel(
    var error: String = "unknown",
    var errorCode: String = "",
    var message: String = "unknown"
) : BaseModel()