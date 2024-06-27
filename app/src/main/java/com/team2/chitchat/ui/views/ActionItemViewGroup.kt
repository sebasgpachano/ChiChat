package com.team2.chitchat.ui.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.team2.chitchat.R
import com.team2.chitchat.databinding.ViewItemActionComponentBinding

class ActionItemViewGroup @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): ConstraintLayout(context, attrs, defStyleAttr) {
    var binding: ViewItemActionComponentBinding =
        ViewItemActionComponentBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.ActionItemViewGroup)
        binding.imageVLeftActionItem.setImageDrawable(attributes.getDrawable(R.styleable.ActionItemViewGroup_icon))
        binding.textVActionItem.text = attributes.getString(R.styleable.ActionItemViewGroup_textAction)
        attributes.recycle()
    }

}