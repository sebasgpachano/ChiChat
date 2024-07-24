package com.team2.chitchat.data.mapper.chats

import android.util.Log
import com.team2.chitchat.data.domain.model.chats.ListChatsModel
import com.team2.chitchat.data.repository.local.chat.ChatDB
import com.team2.chitchat.data.repository.local.message.MessageDB
import com.team2.chitchat.data.repository.local.user.UserDB
import com.team2.chitchat.ui.extensions.TAG
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class ListChatsMapper(
    private val userId: String,
    private val users: ArrayList<UserDB>,
    private val arrayChats: ArrayList<ChatDB>,
    private val arrayMessages: ArrayList<MessageDB>
) {
    fun getList(): ArrayList<ListChatsModel> {
        val mappedList = ArrayList<ListChatsModel>()

        for (chat in arrayChats) {
            val message: MessageDB? = arrayMessages
                .sortedByDescending { it.date }
                .find { it.chatId == chat.id }
            val state = users.find { it.id == chat.idOtherUser }?.online ?: false
            val listChatsModel = ListChatsModel(
                id = chat.id,
                name = chat.otherUserName,
                image = chat.otherUserImg,
                state = state,
                notification = getNotifications(chat),
                lastMessage = message?.message ?: "",
                date = getDate(message),
                view = chat.view
            )
            if (chat.view || listChatsModel.notification > 0) {
                mappedList.add(listChatsModel)
            }
        }
        return formaterDate(mappedList.sortedByDescending { it.date })
    }

    private fun getNotifications(chat: ChatDB): Int {
        val lastMessageSendDate: String? = chat.dateLastMessageSend.ifBlank {
            val lastMessage = arrayMessages
                .sortedByDescending { it.date }
                .find { it.sourceId == userId }
            lastMessage?.date
        }
        val unreadMessagesCount = arrayMessages.count {
            it.chatId == chat.id && !it.view &&
                    (lastMessageSendDate == null || it.date > lastMessageSendDate)
        }

        return unreadMessagesCount
    }

    private fun getDate(message: MessageDB?): String {
        return if (message?.date.isNullOrBlank()) {
            ""
        } else {
            fixedServerHour(message?.date)
        }
    }

    private fun fixedServerHour(messageTime: String?): String {
        val deviceTime = ZonedDateTime.now()
        val offsetInHours = deviceTime.offset.totalSeconds / 3600.0.toLong()
        val formatter = DateTimeFormatter.ISO_DATE_TIME
        val zonedDateTime = ZonedDateTime.parse(messageTime, formatter.withZone(ZoneOffset.UTC))
        val updatedZonedDateTime = zonedDateTime.plusHours(2 + offsetInHours)
        return updatedZonedDateTime.format(formatter)
    }

    private fun formaterDate(list: List<ListChatsModel>): ArrayList<ListChatsModel> {
        val mappedList = ArrayList<ListChatsModel>()
        for (chat in list) {
            if (chat.date.isBlank()) {
                mappedList.add(chat)
            } else {
                val listChatsModel = ListChatsModel(
                    id = chat.id,
                    name = chat.name,
                    image = chat.image,
                    state = chat.state,
                    notification = chat.notification,
                    lastMessage = chat.lastMessage,
                    date = setFormater(chat.date),
                    view = chat.view
                )
                mappedList.add(listChatsModel)
            }
        }
        return mappedList
    }

    private fun setFormater(date: String): String {
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
        val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yy")
        if (date.isBlank()) {
            return ""
        }
        return try {
            val zonedDateTimeUTC = ZonedDateTime.parse(date)
            val localZonedDateTime = zonedDateTimeUTC.withZoneSameInstant(ZoneId.systemDefault())
            val currentDate = LocalDate.now(localZonedDateTime.zone)

            if (localZonedDateTime.toLocalDate().isEqual(currentDate)) {
                localZonedDateTime.format(timeFormatter)
            } else {
                localZonedDateTime.format(dateFormatter)
            }
        } catch (e: DateTimeParseException) {
            Log.e(TAG, "hora> Failed to parse date: $date", e)
            ""
        }
    }
}