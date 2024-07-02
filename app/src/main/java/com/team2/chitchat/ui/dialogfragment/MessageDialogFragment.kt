package com.team2.chitchat.ui.dialogfragment

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.DialogFragment
import androidx.viewpager.widget.ViewPager.LayoutParams
import com.team2.chitchat.R
import com.team2.chitchat.databinding.FragmentDialogErrorMessageBinding
import com.team2.chitchat.ui.extensions.TAG

class MessageDialogFragment: DialogFragment(){
    var iconID: Int? = null
    var title : String? = null
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "-> Oncreate")
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "-> OnStart")
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        Log.d(TAG, "-> onCreateDialog")
        return activity?.let {
            val builder = AlertDialog.Builder(it,R.style.backGroundDialog)
            // Get the layout inflater.
            val inflater = requireActivity().layoutInflater
            binding = FragmentDialogErrorMessageBinding.inflate(inflater)

            builder.setView(binding.root)
            val dialog = builder.create()
            dialog!!.setOnShowListener {
                // Obtenemos el objeto Window para acceder a los atributos de la ventana del diálogo
                val window = dialog.window
                if (window != null) {
                    // Creamos un nuevo objeto LayoutParams para definir el ancho y el alto del diálogo
                    val layoutParams = WindowManager.LayoutParams()

                    // Copiamos los atributos actuales en el nuevo objeto LayoutParams
                    layoutParams.copyFrom(window.attributes)
                    val widthInPixel = convertDpToPx(270)
                    // Definimos el ancho y el alto del diálogo
                    layoutParams.width = widthInPixel
                    layoutParams.height = LayoutParams.WRAP_CONTENT

                    // Centramos el diálogo en la pantalla
                    layoutParams.gravity = Gravity.CENTER

                    // Aplicamos los nuevos atributos a la ventana del diálogo
                    window.attributes = layoutParams
                }
            }
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
        Log.d(TAG, "-> onCreateView")
        binding = FragmentDialogErrorMessageBinding.inflate(inflater)
        paintDialog()
        return binding.root
    }
    override fun onResume() {
        super.onResume()
        Log.d(TAG, "-> onResume")
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
    private fun convertDpToPx(dp: Int): Int {
        val displayMetrics = requireContext().resources.displayMetrics
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT))
    }
    private fun paintDialog() {

        if (iconID != null) {
            binding.imageVMessageDF.apply {
                visibility = View.VISIBLE
                setImageDrawable(AppCompatResources.getDrawable(context,iconID!!))
            }

        } else {
            binding.imageVMessageDF.visibility = View.GONE
        }

        binding.textVMessageErrorDF.text = message

        binding.textVTitleErrorDF.apply {
            visibility = if (title.isNullOrEmpty()) View.GONE else View.VISIBLE
            text = title?:""
        }

        title?.let { binding.textVTitleErrorDF.text = it }

        binding.buttonNegativeErrorDF.apply {
            visibility = if (negativeButton.isNullOrEmpty()) View.GONE else View.VISIBLE
            text = negativeButton?:""
        }
        binding.buttonPositiveErrorDF.text = positiveButton
    }
    private fun initializeListener() {
        binding.buttonPositiveErrorDF.setOnClickListener {
            Log.d("prueba", "Positive initializeListener: ")
            listener?.positiveButtonOnclick(it)
            dismiss()
        }
        negativeButton?.let {
            binding.buttonNegativeErrorDF.setOnClickListener {view->
                Log.d("prueba", "Negative initializeListener: ")
                dismiss()
                listener?.negativeButtonOnclick(view)
            }
        }

    }
}