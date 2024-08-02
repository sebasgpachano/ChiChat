package com.team2.chitchat.ui.contactslist.adapter

import androidx.recyclerview.widget.DiffUtil
import com.team2.chitchat.data.repository.local.user.UserDB

class ContactsListDiffCallback : DiffUtil.ItemCallback<UserDB>() {
    override fun areItemsTheSame(oldItem: UserDB, newItem: UserDB): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: UserDB, newItem: UserDB): Boolean {
        return oldItem == newItem
    }
}