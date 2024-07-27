package com.team2.chitchat.ui.dialogfragment

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.DialogFragment
import com.team2.chitchat.R
import com.team2.chitchat.databinding.FragmentDialogErrorMessageBinding

class MessageDialogFragment : DialogFragment() {
    var iconID: Int? = null
    var title: String? = null
    var message: String = ""
    var positiveButton: String = ""
    var negativeButton: String? = null
    var listener: MessageDialogListener? = null

    companion object {
        const val MESSAGE_DIALOG_TAG = "MESSAGE_DIALOG_TAG"
    }

    private lateinit var binding: FragmentDialogErrorMessageBinding

    interface MessageDialogListener {
        fun positiveButtonOnclick(view: View)
        fun negativeButtonOnclick(view: View) = Unit
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it, R.style.backGroundDialog)
            // Get the layout inflater.
            val inflater = requireActivity().layoutInflater
            binding = FragmentDialogErrorMessageBinding.inflate(inflater)

            builder.setView(binding.root)
            val dialog = builder.create()
            paintDialog()
            initializeListener()
            dialog
        } ?: throw IllegalStateException("Activity cannot be null")


    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDialogErrorMessageBinding.inflate(inflater)
        paintDialog()
        return binding.root
    }

    fun refreshValues(
        iconID: Int? = null,
        title: String? = null,
        message: String,
        positiveButton: String,
        negativeButton: String? = null,
        listener: MessageDialogListener
    ) {
        this.iconID = iconID
        this.title = title
        this.message = message
        this.positiveButton = positiveButton
        this.negativeButton = negativeButton
        this.listener = listener
        paintDialog()
        initializeListener()
    }

    private fun paintDialog() {

        if (iconID != null) {
            binding.imageVMessageDF.apply {
                visibility = View.VISIBLE
                setImageDrawable(AppCompatResources.getDrawable(context, iconID!!).apply { maxHeight = 24 })
            }

        } else {
            binding.imageVMessageDF.visibility = View.GONE
        }

        binding.textVMessageErrorDF.text = message

        binding.textVTitleErrorDF.apply {
            visibility = if (title.isNullOrEmpty()) View.GONE else View.VISIBLE
            text = title ?: ""
        }

        title?.let { binding.textVTitleErrorDF.text = it }

        binding.buttonNegativeErrorDF.apply {
            visibility = if (negativeButton.isNullOrEmpty()) View.GONE else View.VISIBLE
            text = negativeButton ?: ""
        }
        binding.buttonPositiveErrorDF.text = positiveButton
    }

    private fun initializeListener() {
        binding.buttonPositiveErrorDF.setOnClickListener {
            listener?.positiveButtonOnclick(it)
            dismiss()
        }
        negativeButton?.let {
            binding.buttonNegativeErrorDF.setOnClickListener { view ->
                dismiss()
                listener?.negativeButtonOnclick(view)
            }
        }

    }
}