package com.team2.chitchat.data.mapper.messages

import com.team2.chitchat.data.domain.model.messages.GetMessagesModel
import com.team2.chitchat.data.repository.local.message.MessageDB
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import javax.inject.Inject

class MessagesMapper @Inject constructor() {
    fun getMessages(messages: List<MessageDB>): List<GetMessagesModel> {
        return messages.map { message ->
            GetMessagesModel(
                id = message.id,
                chatId = message.chatId,
                sourceId = message.sourceId,
                message = message.message,
                date = formatDate(message.date),
                view = message.view
            )
        }
    }

    private fun formatDate(date: String): String {
        return try {
            val formatter = DateTimeFormatter.ISO_DATE_TIME
            val zonedDateTime = ZonedDateTime.parse(fixedServerHour(date), formatter)
            val localDateTime = zonedDateTime.withZoneSameInstant(ZoneId.systemDefault())
            localDateTime.format(DateTimeFormatter.ofPattern("HH:mm"))
        } catch (e: DateTimeParseException) {
            ""
        }
    }

    private fun fixedServerHour(messageTime: String?): String {
        val formatter = DateTimeFormatter.ISO_DATE_TIME
        val zonedDateTime = ZonedDateTime.parse(messageTime, formatter)
        val updatedZonedDateTime = zonedDateTime.plusHours(2)
        return updatedZonedDateTime.format(formatter)
    }
}