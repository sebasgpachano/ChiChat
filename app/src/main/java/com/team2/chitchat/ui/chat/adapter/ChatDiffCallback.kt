package com.team2.chitchat.ui.chat.adapter

import androidx.recyclerview.widget.DiffUtil
import com.team2.chitchat.data.domain.model.messages.GetMessagesModel

class ChatDiffCallback : DiffUtil.ItemCallback<GetMessagesModel>() {
    override fun areItemsTheSame(oldItem: GetMessagesModel, newItem: GetMessagesModel): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: GetMessagesModel, newItem: GetMessagesModel): Boolean {
        return oldItem == newItem
    }
}