package com.team2.chitchat.ui.registration.adapter

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class SpaceItemDecoration(private val space: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        val isLastItem = position == parent.adapter?.itemCount?.minus(1)

        outRect.right = space

        if (isLastItem) {
            outRect.right = 0
        }
    }
}