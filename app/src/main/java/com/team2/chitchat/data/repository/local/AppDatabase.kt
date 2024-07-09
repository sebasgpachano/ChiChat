package com.team2.chitchat.data.repository.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.team2.chitchat.data.repository.local.chat.ChatDAO
import com.team2.chitchat.data.repository.local.chat.ChatDB
import com.team2.chitchat.data.repository.local.message.MessageDB
import com.team2.chitchat.data.repository.local.message.MessagesDAO
import com.team2.chitchat.data.repository.local.user.UserDAO
import com.team2.chitchat.data.repository.local.user.UserDB

@Database(entities = [UserDB::class, MessageDB::class, ChatDB::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDAO(): UserDAO
    abstract fun chatDAO(): ChatDAO
    abstract fun messagesDAO(): MessagesDAO
}