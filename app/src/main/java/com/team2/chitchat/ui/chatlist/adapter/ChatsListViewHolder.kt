package com.team2.chitchat.ui.chatlist.adapter

import android.graphics.PorterDuff
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.team2.chitchat.R
import com.team2.chitchat.data.domain.model.chats.ListChatsModel
import com.team2.chitchat.databinding.ItemRecyclerviewListChatsBinding
import com.team2.chitchat.ui.extensions.gone

class ChatsListViewHolder(
    private val binding: ItemRecyclerviewListChatsBinding,
    private val listChatsAdapterListener: ChatsListAdapter.ListChatsAdapterListener
) :
    RecyclerView.ViewHolder(binding.root) {
    fun onBind(chatList: ListChatsModel, position: Int) {
        binding.apply {
            tvName.text = chatList.name
            tvMsg.text = chatList.lastMessage
            tvDate.text = chatList.date
            if (chatList.state) {
                ivState.setColorFilter(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.greenEnabled
                    ), PorterDuff.Mode.SRC_IN
                )
            }
            if (chatList.notification == 0) {
                tvNotification.gone()
            }
            binding.root.setOnClickListener {
                listChatsAdapterListener.onItemClick(chatList.id)
            }
        }
    }
}