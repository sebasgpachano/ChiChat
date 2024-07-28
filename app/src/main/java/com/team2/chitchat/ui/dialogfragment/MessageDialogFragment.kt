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

        fun newInstance(
            iconID: Int? = null,
            title: String? = null,
            message: String,
            positiveButton: String,
            negativeButton: String? = null,
            listener: MessageDialogListener? = null
        ): MessageDialogFragment {
            return MessageDialogFragment().apply {
                refreshValues(
                    iconID = iconID,
                    title = title,
                    message = message,
                    positiveButton = positiveButton,
                    negativeButton = negativeButton,
                    listener = listener
                )
            }
        }
    }

    private lateinit var binding: FragmentDialogErrorMessageBinding

    interface MessageDialogListener {
        fun positiveButtonOnclick(view: View)
        fun negativeButtonOnclick() = Unit
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it, R.style.backGroundDialog)

            val inflater = requireActivity().layoutInflater
            binding = FragmentDialogErrorMessageBinding.inflate(inflater)

            builder.setView(binding.root)
            isCancelable = false
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
        listener: MessageDialogListener? = null
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
        binding.apply {
            iconID?.let {
                imageVMessageDF.apply {
                    visibility = View.VISIBLE
                    setImageDrawable(
                        AppCompatResources.getDrawable(context, it).apply {
                            maxHeight = 24
                        }
                    )
                }
            } ?: run {
                imageVMessageDF.visibility = View.GONE
            }

            textVTitleErrorDF.apply {
                visibility = if (title.isNullOrEmpty()) View.GONE else View.VISIBLE
                text = title ?: ""
            }

            textVMessageErrorDF.text = message

            buttonNegativeErrorDF.apply {
                visibility = if (negativeButton.isNullOrEmpty()) View.GONE else View.VISIBLE
                text = negativeButton ?: ""
            }

            buttonPositiveErrorDF.text = positiveButton
        }
    }

    private fun initializeListener() {
        binding.apply {
            buttonPositiveErrorDF.setOnClickListener {
                listener?.positiveButtonOnclick(it) // Safe call
                dismiss()
            }
            buttonNegativeErrorDF.setOnClickListener {
                listener?.negativeButtonOnclick() // Safe call
                dismiss()
            }
        }
    }
}