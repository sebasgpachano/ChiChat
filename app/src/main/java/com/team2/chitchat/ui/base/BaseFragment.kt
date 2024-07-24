package com.team2.chitchat.ui.base

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Shader
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.team2.chitchat.R
import com.team2.chitchat.data.repository.remote.backend.BaseService
import com.team2.chitchat.ui.dialogfragment.MessageDialogFragment

abstract class BaseFragment<B : ViewBinding> : Fragment() {
    var binding: B? = null
    private lateinit var baseActivity: BaseActivity<*>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        baseActivity = activity as BaseActivity<*>
    }

    override fun onResume() {
        super.onResume()
        configureToolbarAndConfigScreenSections()
        updateProfileImage()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        inflateBinding()
        createViewAfterInflateBinding(inflater, container, savedInstanceState)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
        observeViewModel()
        viewCreatedAfterSetupObserverViewModel(view, savedInstanceState)
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    fun hideToolbar() {
        baseActivity.hideToolbar()
    }

    fun showToolbar(
        showBack: Boolean = false,
        showProfile: Boolean = false,
        showNotification: Boolean = false,
        title: String = "",
    ) {
        baseActivity.showToolbar(
            showBack = showBack,
            showNotification = showNotification,
            showProfile = showProfile,
            title = title,
        )
    }

    fun updateShowToolbarBack(showBack: Boolean) {
        baseActivity.updateShowToolbarBack(showBack)
    }

    fun updateShowToolbarProfile(showProfile: Boolean) {
        baseActivity.updateShowToolbarProfile(showProfile)
    }

    fun updateShowToolbarNotification(showNotification: Boolean) {
        baseActivity.updateShowToolbarNotification(showNotification)
    }

    fun updateShowToolbarTitle(title: String) {
        baseActivity.updateShowToolbarTitle(title)
    }

    fun showLoading(show: Boolean) {
        baseActivity.showLoading(show)
    }


    fun fragmentFullScreenLayoutWithoutToolbar() {
        baseActivity.fragmentFullScreenLayoutWithoutToolbar()
    }

    fun fragmentLayoutWithToolbar() {
        baseActivity.fragmentLayoutWithToolbar()
    }

    abstract fun inflateBinding()
    abstract fun createViewAfterInflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    )

    fun showDialogError(errorCode: String, listener: (view: View) -> Unit) {
        val message = when (errorCode) {
            BaseService.ERROR_UNAUTHORIZED.toString() -> getString(R.string.error_401)
            BaseService.ERROR_FORBIDDEN.toString() -> getString(R.string.error_403)
            BaseService.ERROR_INTERNAL_SERVER.toString() -> getString(R.string.error_500)
            else -> getString(R.string.error_unknown)
        }
        showErrorMessage(
            message = message,
            listener = object : MessageDialogFragment.MessageDialogListener {
                override fun positiveButtonOnclick(view: View) {
                    listener(view)
                }

            }
        )
    }

    fun showErrorMessage(
        message: String,
        listener: MessageDialogFragment.MessageDialogListener
    ) {
        baseActivity.showMessageWithOneButton(
            iconID = R.drawable.error_24px,
            title = context?.getString(R.string.error),
            message = message,
            textPositiveButton = resources.getString(R.string.accept),
            listener = listener
        )
    }

    fun showMessageDialog(
        iconID: Int,
        title: String,
        message: String,
        textPositiveButton: String = resources.getString(R.string.accept),
        textNegativeButton: String = resources.getString(R.string.cancel),
        listener: MessageDialogFragment.MessageDialogListener
    ) {
        baseActivity.showMessageWithTwoButton(
            iconID = iconID,
            title = title,
            message = message,
            textPositiveButton = textPositiveButton,
            textNegativeButton = textNegativeButton,
            listener = listener
        )
    }

    private fun updateProfileImage() {
        val bitmap = loadProfilePictureFromSharedPreferences()
        bitmap?.let {
            val circularBitmap = getCircularBitmap(it)
            baseActivity.setProfileImage(circularBitmap)
        }
    }

    private fun loadProfilePictureFromSharedPreferences(): Bitmap? {
        val sharedPreferences =
            requireContext().getSharedPreferences(
                "my_preferences",
                Context.MODE_PRIVATE
            )
        val imageString = sharedPreferences.getString(getString(R.string.profile_picture), null)
        return if (imageString != null) {
            val byteArray = Base64.decode(imageString, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        } else {
            null
        }
    }

    private fun getCircularBitmap(bitmap: Bitmap): Bitmap {
        val size = minOf(bitmap.width, bitmap.height)
        val x = (bitmap.width - size) / 2
        val y = (bitmap.height - size) / 2

        val squaredBitmap = Bitmap.createBitmap(bitmap, x, y, size, size)
        if (squaredBitmap != bitmap) {
            bitmap.recycle()
        }

        val circularBitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(circularBitmap)
        val paint = Paint().apply {
            isAntiAlias = true
            shader = BitmapShader(squaredBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        }

        val radius = size / 2f
        canvas.drawCircle(radius, radius, radius, paint)

        return circularBitmap
    }

    abstract fun configureToolbarAndConfigScreenSections()

    protected open fun setupViewModel() = Unit

    abstract fun observeViewModel()

    abstract fun viewCreatedAfterSetupObserverViewModel(view: View, savedInstanceState: Bundle?)
}