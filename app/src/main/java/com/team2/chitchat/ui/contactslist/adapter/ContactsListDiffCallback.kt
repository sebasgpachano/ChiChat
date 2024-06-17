package com.team2.chitchat.ui.contactslist.adapter

import androidx.recyclerview.widget.DiffUtil
import com.team2.chitchat.data.domain.model.users.GetUserModel

class ContactsListDiffCallback : DiffUtil.ItemCallback<GetUserModel>() {
    override fun areItemsTheSame(oldItem: GetUserModel, newItem: GetUserModel): Boolean {
        return oldItem.login == newItem.login
    }

    override fun areContentsTheSame(oldItem: GetUserModel, newItem: GetUserModel): Boolean {
        return oldItem == newItem
    }
}