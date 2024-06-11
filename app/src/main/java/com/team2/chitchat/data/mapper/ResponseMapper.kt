package com.team2.chitchat.data.mapper

interface ResponseMapper<E, M> {
    fun fromResponse(response: E): M
}