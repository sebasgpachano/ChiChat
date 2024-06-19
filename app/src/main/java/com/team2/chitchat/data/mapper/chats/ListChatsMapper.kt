package com.team2.chitchat.data.mapper.chats

import com.team2.chitchat.data.domain.model.chats.GetChatsModel
import com.team2.chitchat.data.domain.model.chats.ListChatsModel
import com.team2.chitchat.data.domain.model.messages.GetMessagesModel
import com.team2.chitchat.data.domain.model.users.GetUserModel
import com.team2.chitchat.data.sesion.DataUserSession

class ListChatsMapper(
    private val dataUserSession: DataUserSession,
    private val arrayChats: ArrayList<GetChatsModel>,
    private val arrayUsers: ArrayList<GetUserModel>,
    private val arrayMessages: ArrayList<GetMessagesModel>
) {
    fun getList(): ArrayList<ListChatsModel> {
        val mappedList = ArrayList<ListChatsModel>()

        val filteredChats = arrayChats.filter { chat ->
            chat.source == dataUserSession.id || chat.target == dataUserSession.id
        }
        for (chat in filteredChats) {
            val user: GetUserModel? = if (chat.source != dataUserSession.id) {
                arrayUsers.find { it.login == chat.source }
            } else {
                arrayUsers.find { it.login == chat.target }
            }

            val messages: GetMessagesModel? = arrayMessages.find { it.chatId == chat.id }

            if (user != null) {
                val listChatsModel = ListChatsModel(
                    chat.id,
                    user.nick,
                    user.avatar,
                    user.online,
                    0,
                    messages?.message ?: "",
                    messages?.date ?: ""
                )
                mappedList.add(listChatsModel)
            }
        }

        return mappedList
    }

}