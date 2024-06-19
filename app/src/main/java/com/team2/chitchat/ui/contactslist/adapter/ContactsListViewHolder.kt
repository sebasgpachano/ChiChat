package com.team2.chitchat.ui.contactslist.adapter

import androidx.recyclerview.widget.RecyclerView
import com.team2.chitchat.data.domain.model.users.GetUserModel
import com.team2.chitchat.databinding.ItemRecyclerviewListContactsBinding

class ContactsListViewHolder(val binding: ItemRecyclerviewListContactsBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun onBind(getUserModel: GetUserModel) {
        binding.apply {
            tvName.text = getUserModel.nick
        }
    }
}