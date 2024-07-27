package com.team2.chitchat.data.mapper

fun interface ResponseMapper<E, M> {
    fun fromResponse(response: E): M
}