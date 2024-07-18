package com.team2.chitchat.data.mapper.chats

import com.team2.chitchat.data.domain.model.chats.ListChatsModel
import com.team2.chitchat.data.repository.local.chat.ChatDB
import com.team2.chitchat.data.repository.local.message.MessageDB
import com.team2.chitchat.data.repository.local.user.UserDB
import java.time.LocalDate
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

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

        return ArrayList(mappedList.sortedByDescending { it.date })
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
            val localZonedDateTime = ZonedDateTime.parse(fixedServerHour(message?.date))
            val formattedDate = when {
                localZonedDateTime.toLocalDate()
                    .isEqual(LocalDate.now(localZonedDateTime.zone)) -> {
                    localZonedDateTime.format(DateTimeFormatter.ofPattern("HH:mm"))
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