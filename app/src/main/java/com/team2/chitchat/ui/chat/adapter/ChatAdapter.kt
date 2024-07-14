package com.team2.chitchat.ui.chat.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.team2.chitchat.data.domain.model.messages.GetMessagesModel
import com.team2.chitchat.databinding.ItemMessageReceivedBinding
import com.team2.chitchat.databinding.ItemMessageSentBinding
import com.team2.chitchat.hilt.SimpleApplication
import com.team2.chitchat.ui.chat.adapter.viewholder.ChatViewHolder

class ChatAdapter(private val chatAdapterListener: ChatAdapterListener) :
    ListAdapter<GetMessagesModel, ChatViewHolder>(ChatDiffCallback()) {

    companion object {
        private const val VIEW_TYPE_RECEIVED = 1
        private const val VIEW_TYPE_SENT = 2
    }

    interface ChatAdapterListener {
        fun onItemClick(messageId: String)
    }

    override fun getItemViewType(position: Int): Int {
        val message = getItem(position)
        return if (message.sourceId == SimpleApplication.INSTANCE.getUserID()) {
            VIEW_TYPE_SENT
        } else {
            VIEW_TYPE_RECEIVED
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        return when (viewType) {
            VIEW_TYPE_RECEIVED -> {
                val binding = ItemMessageReceivedBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                ChatViewHolder.ReceivedMessageViewHolder(binding, chatAdapterListener)
            }

            VIEW_TYPE_SENT -> {
                val binding = ItemMessageSentBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                ChatViewHolder.SentMessageViewHolder(binding, chatAdapterListener)
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val message = getItem(position)
        when (holder) {
            is ChatViewHolder.ReceivedMessageViewHolder -> holder.onBind(message)
            is ChatViewHolder.SentMessageViewHolder -> holder.onBind(message)
        }
    }

}