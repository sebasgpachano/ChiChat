package com.team2.chitchat.data.mapper.chats

import android.content.Context
import com.team2.chitchat.R
import com.team2.chitchat.data.domain.model.chats.ListChatsModel
import com.team2.chitchat.data.repository.local.chat.ChatDB
import com.team2.chitchat.data.repository.local.message.MessageDB
import java.time.LocalDate
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class ListChatsMapper(
    private val arrayChats: ArrayList<ChatDB>,
    private val arrayMessages: ArrayList<MessageDB>,
    private val context: Context
) {
    fun getList(): ArrayList<ListChatsModel> {
        val mappedList = ArrayList<ListChatsModel>()

        for (chat in arrayChats) {
            val message: MessageDB? = arrayMessages
                .sortedByDescending { it.date }
                .find { it.chatId == chat.id }
            val listChatsModel = ListChatsModel(
                id = chat.id,
                name = chat.otherUserName,
                image = chat.otherUserImg,
                state = chat.otherUserOnline,
                notification = 0,
                lastMessage = message?.message ?: "",
                getDate(message)
            )
            mappedList.add(listChatsModel)
        }

        return ArrayList(mappedList.sortedByDescending { it.date })
    }

    private fun getDate(message: MessageDB?): String {
        return if (message?.date.isNullOrBlank()) {
            ""
        } else {
            val localZonedDateTime = ZonedDateTime.parse(fixedServerHour(message?.date))
            val formattedDate = when {
                localZonedDateTime.toLocalDate()
                    .isEqual(LocalDate.now(localZonedDateTime.zone)) -> {
                    localZonedDateTime.format(DateTimeFormatter.ofPattern("HH:mm"))
                }

                localZonedDateTime.toLocalDate()
                    .isEqual(LocalDate.now(localZonedDateTime.zone).minusDays(1)) -> {
                    context.getString(R.string.chat_list_date_yesterday)
                }

                else -> {
                    localZonedDateTime.format(DateTimeFormatter.ofPattern("dd/MM/yy"))
                }
            }
            formattedDate
        }
    }

    private fun fixedServerHour(messageTime: String?): String {
        val formatter = DateTimeFormatter.ISO_DATE_TIME
        val zonedDateTime = ZonedDateTime.parse(messageTime, formatter)
        val updatedZonedDateTime = zonedDateTime.plusHours(2)
        return updatedZonedDateTime.format(formatter)
    }
}