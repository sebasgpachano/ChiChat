package com.team2.chitchat.ui.contactslist.adapter

import androidx.recyclerview.widget.RecyclerView
import com.team2.chitchat.data.domain.model.users.GetUserModel
import com.team2.chitchat.databinding.ItemRecyclerviewListContactsBinding
import com.team2.chitchat.ui.contactslist.adapter.ContactsListAdapter.ContactsListAdapterListener

class ContactsListViewHolder(
    val binding: ItemRecyclerviewListContactsBinding,
    private val contactsListAdapterListener: ContactsListAdapterListener
) : RecyclerView.ViewHolder(binding.root) {

    fun onBind(getUserModel: GetUserModel) {
        binding.apply {
            tvName.text = getUserModel.nick
        }
        binding.root.setOnClickListener {
            contactsListAdapterListener.onItemClick(getUserModel.id)
        }
    }
}