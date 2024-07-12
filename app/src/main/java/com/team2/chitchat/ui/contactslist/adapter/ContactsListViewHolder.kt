package com.team2.chitchat.ui.contactslist.adapter

import androidx.recyclerview.widget.RecyclerView
import com.team2.chitchat.data.repository.local.user.UserDB
import com.team2.chitchat.databinding.ItemRecyclerviewListContactsBinding
import com.team2.chitchat.ui.contactslist.adapter.ContactsListAdapter.ContactsListAdapterListener

class ContactsListViewHolder(
    val binding: ItemRecyclerviewListContactsBinding,
    private val contactsListAdapterListener: ContactsListAdapterListener
) : RecyclerView.ViewHolder(binding.root) {

    fun onBind(user: UserDB) {
        binding.apply {
            tvName.text = user.nick
        }
        binding.root.setOnClickListener {
            contactsListAdapterListener.onItemClick(user.id)
        }
    }
}