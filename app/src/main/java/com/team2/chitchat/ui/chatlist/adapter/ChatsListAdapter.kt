package com.team2.chitchat.ui.chatlist.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.team2.chitchat.data.domain.model.chats.ListChatsModel
import com.team2.chitchat.databinding.ItemRecyclerviewListChatsBinding

class ChatsListAdapter(private val listChatsAdapterListener: ListChatsAdapterListener) :
    ListAdapter<ListChatsModel, ChatsListViewHolder>(ChatsListDiffCallback()) {
    fun interface ListChatsAdapterListener {
        fun onItemClick(idChat: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatsListViewHolder {
        return ChatsListViewHolder(
            ItemRecyclerviewListChatsBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            ), listChatsAdapterListener
        )
    }

    fun getItemSelected(position: Int): ListChatsModel {
        return getItem(position)
    }

    override fun onBindViewHolder(holder: ChatsListViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }
}