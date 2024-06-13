package com.team2.chitchat.ui.contactslist.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.team2.chitchat.data.domain.model.users.GetUserModel
import com.team2.chitchat.databinding.ItemRecyclerviewListContactsBinding

class ContactsListAdapater :
    ListAdapter<GetUserModel, ContactsListViewHolder>(ContactsListDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsListViewHolder {
        return ContactsListViewHolder(
            ItemRecyclerviewListContactsBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ContactsListViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }
}