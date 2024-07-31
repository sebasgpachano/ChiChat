package com.team2.chitchat.ui.chat.adapter.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.team2.chitchat.data.domain.model.messages.GetMessagesModel
import com.team2.chitchat.databinding.ItemMessageReceivedBinding
import com.team2.chitchat.databinding.ItemMessageSentBinding
import com.team2.chitchat.ui.chat.adapter.ChatAdapter

sealed class ChatViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    class ReceivedMessageViewHolder(
        private val binding: ItemMessageReceivedBinding,
        private val chatAdapterListener: ChatAdapter.ChatAdapterListener
    ) : ChatViewHolder(binding.root) {
        fun onBind(message: GetMessagesModel) {
            binding.apply {
                tvMessageReceived.text = message.message
                tvTimeMessageReceived.text = message.date
                root.setOnClickListener {
                    chatAdapterListener.onItemClick()
                }
            }
        }
    }

    class SentMessageViewHolder(
        private val binding: ItemMessageSentBinding,
        private val chatAdapterListener: ChatAdapter.ChatAdapterListener
    ) : ChatViewHolder(binding.root) {
        fun onBind(message: GetMessagesModel) {
            binding.apply {
                tvMessageSent.text = message.message
                tvTimeMessageSent.text = message.date
                root.setOnClickListener {
                    chatAdapterListener.onItemClick()
                }
            }
        }
    }
}