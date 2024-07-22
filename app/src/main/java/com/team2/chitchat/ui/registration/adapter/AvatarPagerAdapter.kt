package com.team2.chitchat.ui.registration.adapter

import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.team2.chitchat.databinding.ItemAvatarBinding

class AvatarPagerAdapter(
    private val avatarList: List<Int>,
    private val onAvatarClick: (Int) -> Unit
) : RecyclerView.Adapter<AvatarPagerAdapter.AvatarViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvatarViewHolder {
        val binding = ItemAvatarBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AvatarViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AvatarViewHolder, position: Int) {
        val avatarResId = avatarList[position]
        holder.onBind(avatarResId, onAvatarClick)
    }

    override fun getItemCount(): Int = avatarList.size

    class AvatarViewHolder(private val binding: ItemAvatarBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(avatarResId: Int, onAvatarClick: (Int) -> Unit) {
            binding.ivAvatar.setImageResource(avatarResId)
            binding.root.setOnClickListener {
                onAvatarClick(avatarResId)
            }
        }
    }

    class SpaceItemDecoration(private val space: Int) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            super.getItemOffsets(outRect, view, parent, state)
            outRect.right = space
        }
    }
}

