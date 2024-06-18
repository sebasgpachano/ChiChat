package com.team2.chitchat.data.mapper.chats

import android.content.Context
import com.team2.chitchat.R
import com.team2.chitchat.data.domain.model.chats.GetChatsModel
import com.team2.chitchat.data.domain.model.chats.ListChatsModel
import com.team2.chitchat.data.domain.model.messages.GetMessagesModel
import com.team2.chitchat.data.domain.model.users.GetUserModel
import com.team2.chitchat.data.sesion.DataUserSession
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class ListChatsMapper(
    private val dataUserSession: DataUserSession,
    private val arrayChats: ArrayList<GetChatsModel>,
    private val arrayUsers: ArrayList<GetUserModel>,
    private val arrayMessages: ArrayList<GetMessagesModel>,
    private val context: Context
) {
    fun getList(): ArrayList<ListChatsModel> {
        val mappedList = ArrayList<ListChatsModel>()

        for (chat in getChatsUser()) {
            val user = getUser(chat)
            val message: GetMessagesModel? = arrayMessages.find { it.chatId == chat.id }

            if (user != null) {
                val listChatsModel = ListChatsModel(
                    chat.id,
                    user.nick,
                    user.avatar,
                    user.online,
                    0,
                    message?.message ?: "",
                    getDate(message)
                )
                mappedList.add(listChatsModel)
            }
        }

        return mappedList
    }

    private fun getChatsUser(): List<GetChatsModel> {
        return arrayChats.filter { chat ->
            chat.source == dataUserSession.id || chat.target == dataUserSession.id
        }
    }

    private fun getUser(chat: GetChatsModel): GetUserModel? {
        return if (chat.source != dataUserSession.id) {
            arrayUsers.find { it.login == chat.source }
        } else {
            arrayUsers.find { it.login == chat.target }
        }
    }

    private fun getDate(message: GetMessagesModel?): String {
        return if (message?.date.isNullOrBlank()) {
            ""
        } else {
            val formatter = DateTimeFormatter.ISO_DATE_TIME
            val messageZonedDateTime = ZonedDateTime.parse(message?.date, formatter)
            val currentZonedDateTime = ZonedDateTime.now()

            val formattedDate = when {
                messageZonedDateTime.toLocalDate().isEqual(currentZonedDateTime.toLocalDate()) -> {
                    messageZonedDateTime.format(DateTimeFormatter.ofPattern("HH:mm"))
                }

                messageZonedDateTime.toLocalDate()
                    .isEqual(currentZonedDateTime.toLocalDate().minusDays(1)) -> {
                    context.getString(R.string.chat_list_date_yesterday)
                }

                else -> {
                    messageZonedDateTime.format(DateTimeFormatter.ofPattern("dd/MM/yy"))
                }
            }
            formattedDate
        }
    }
}