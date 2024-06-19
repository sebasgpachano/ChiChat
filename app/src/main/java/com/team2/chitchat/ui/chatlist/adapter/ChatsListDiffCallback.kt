package com.team2.chitchat.ui.chatlist.adapter

import androidx.recyclerview.widget.DiffUtil
import com.team2.chitchat.data.domain.model.chats.ListChatsModel

class ChatsListDiffCallback : DiffUtil.ItemCallback<ListChatsModel>() {
    override fun areItemsTheSame(oldItem: ListChatsModel, newItem: ListChatsModel): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ListChatsModel, newItem: ListChatsModel): Boolean {
        return oldItem == newItem
    }
}