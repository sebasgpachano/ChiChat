package com.team2.chitchat.data.domain.model.error

import com.team2.chitchat.data.domain.model.BaseModel

class ErrorModel(
    var error: String = "unknow",
    var errorCode: String = "",
    var message: String = "unknow"
) : BaseModel()