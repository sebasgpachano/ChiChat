package com.team2.chitchat.ui.registration.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.team2.chitchat.databinding.ItemAvatarBinding

class AvatarAdapter(private val avatars: List<Avatar>) :
    RecyclerView.Adapter<AvatarAdapter.AvatarViewHolder>() {

    inner class AvatarViewHolder(val binding: ItemAvatarBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvatarViewHolder {
        val binding = ItemAvatarBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AvatarViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AvatarViewHolder, position: Int) {
        val avatar = avatars[position]
        holder.binding.ivAvatar.setImageResource(avatar.imageResId)
    }

    override fun getItemCount() = avatars.size
}