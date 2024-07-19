package com.team2.chitchat.ui.chat.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.team2.chitchat.data.domain.model.messages.GetMessagesModel
import com.team2.chitchat.data.session.DataUserSession
import com.team2.chitchat.databinding.ItemMessageReceivedBinding
import com.team2.chitchat.databinding.ItemMessageSentBinding
import com.team2.chitchat.ui.chat.adapter.viewholder.ChatViewHolder
import com.team2.chitchat.ui.extensions.TAG
import javax.inject.Inject

class ChatAdapter @Inject constructor(
    private val dataUserSession: DataUserSession
) : ListAdapter<GetMessagesModel, ChatViewHolder>(ChatDiffCallback()) {
    private lateinit var chatAdapterListener: ChatAdapterListener

    companion object {
        private const val VIEW_TYPE_RECEIVED = 1
        private const val VIEW_TYPE_SENT = 2
    }
    fun setListener(listener: ChatAdapterListener) {
        chatAdapterListener = listener
    }
    interface ChatAdapterListener {
        fun onItemClick(messageId: String)
    }

    override fun getItemViewType(position: Int): Int {
        val message = getItem(position)
        Log.d(TAG, "DataUserSession: $dataUserSession")
        return if (message.sourceId == dataUserSession.userId) {
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

    fun submitListWithScroll(newList: List<GetMessagesModel>, recyclerView: RecyclerView?) {
        submitList(newList) {
            recyclerView?.scrollToPosition(itemCount - 1)
        }
    }

}