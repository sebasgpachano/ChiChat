package com.team2.chitchat.data.mapper.chats

import android.content.Context
import com.team2.chitchat.R
import com.team2.chitchat.data.domain.model.chats.GetChatsModel
import com.team2.chitchat.data.domain.model.chats.ListChatsModel
import com.team2.chitchat.data.domain.model.messages.GetMessagesModel
import com.team2.chitchat.data.domain.model.users.GetUserModel
import com.team2.chitchat.data.sesion.DataUserSession
import java.time.LocalDate
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

        for (chat in getChats()) {
            val user = getUser(chat)
            val message: GetMessagesModel? = arrayMessages
                .sortedByDescending { it.date }
                .find { it.chatId == chat.id }

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

    private fun getChats(): List<GetChatsModel> {
        return arrayChats.filter { chat ->
            chat.source == dataUserSession.id || chat.target == dataUserSession.id
        }
    }

    private fun getUser(chat: GetChatsModel): GetUserModel? {
        return if (chat.source != dataUserSession.id) {
            arrayUsers.find { it.id == chat.source }
        } else {
            arrayUsers.find { it.id == chat.target }
        }
    }

    private fun getDate(message: GetMessagesModel?): String {
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