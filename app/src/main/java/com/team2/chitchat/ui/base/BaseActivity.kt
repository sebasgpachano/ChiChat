package com.team2.chitchat.ui.base

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.NavController
import androidx.viewbinding.ViewBinding
import com.team2.chitchat.R
import com.team2.chitchat.data.analytics.FirebaseAnalyticsManager
import com.team2.chitchat.ui.dialogfragment.LoadingDialogFragment
import com.team2.chitchat.ui.dialogfragment.LoadingDialogFragment.Companion.LOADING_DIALOG_FRAGMENT_TAG
import com.team2.chitchat.ui.dialogfragment.MessageDialogFragment
import com.team2.chitchat.ui.extensions.gone
import com.team2.chitchat.ui.extensions.visible
import javax.inject.Inject

abstract class BaseActivity<B : ViewBinding> : AppCompatActivity(), View.OnClickListener {
    lateinit var binding: B
    lateinit var navController: NavController

    private var clToolbar: ConstraintLayout? = null
    private var tbToolbar: Toolbar? = null
    private var ibToolbarBack: ImageButton? = null
    private var tvToolbarTitle: TextView? = null
    private var ibToolbarProfile: ImageButton? = null
    private var ibToolbarNotification: ImageButton? = null

    private var loadingDialogFragment: LoadingDialogFragment = LoadingDialogFragment()
    private val messageDialogFragment: MessageDialogFragment = MessageDialogFragment()

    @Inject
    lateinit var baseActivityControlShowLoading: BaseActivityControlShowLoading

    @Inject
    lateinit var firebaseAnalyticsManager: FirebaseAnalyticsManager

    override fun onResume() {
        super.onResume()
        configureToolbarAndConfigScreenSections()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        callViewModelSaveData()
        super.onSaveInstanceState(outState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inflateBinding()
        setContentView(binding.root)
        findViewByIdToolbar()
        observeViewModel()
        createAfterInflateBindingSetupObserverViewModel()
        setListenersClickToolbarButtons()
    }

    fun showLoading(show: Boolean) {
        if (show) {
            if (baseActivityControlShowLoading.canShowLoading(
                    supportFragmentManager,
                    LOADING_DIALOG_FRAGMENT_TAG
                )
            ) {
                loadingDialogFragment.show(
                    supportFragmentManager,
                    LOADING_DIALOG_FRAGMENT_TAG
                )
            }
        } else {
            if (baseActivityControlShowLoading.canHideLoading(
                    supportFragmentManager,
                    LOADING_DIALOG_FRAGMENT_TAG
                )
            ) {
                loadingDialogFragment.dismiss()
            }
        }
    }

    private fun findViewByIdToolbar() {
        clToolbar = findViewById(R.id.clToolbar)
        tbToolbar = findViewById(R.id.tbToolbar)
        ibToolbarBack = findViewById(R.id.ibToolbarBack)
        tvToolbarTitle = findViewById(R.id.tvToolbarTitle)
        ibToolbarProfile = findViewById(R.id.ibToolbarProfile)
        ibToolbarNotification = findViewById(R.id.ibToolbarNotification)
    }

    fun showToolbar(
        showBack: Boolean = false,
        showProfile: Boolean = false,
        showNotification: Boolean = false,
        title: String = "",
    ) {
        hideAllElementToolbar()
        clToolbar?.visible()
        if (showBack) {
            ibToolbarBack?.visible()
        }
        if (title.isNotBlank()) {
            tvToolbarTitle?.visible()
            tvToolbarTitle?.text = title
        }
        if (showProfile) {
            ibToolbarProfile?.visible()
        }
        if (showNotification) {
            ibToolbarNotification?.visible()
        }
    }

    private fun setListenersClickToolbarButtons() {
        ibToolbarBack?.setOnClickListener(this)
        ibToolbarProfile?.setOnClickListener(this)
        ibToolbarNotification?.setOnClickListener(this)
    }

    fun setProfileImage(imageBitmap: Bitmap) {
        ibToolbarProfile?.setImageBitmap(imageBitmap)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.ibToolbarBack -> clickToolbarBack()
            R.id.ibToolbarProfile -> clickToolbarProfile()
            R.id.ibToolbarNotification -> clickToolbarNotification()
        }
    }

    protected open fun clickToolbarBack() {
        onBackPressedDispatcher.onBackPressed()
    }

    protected open fun clickToolbarNotification() {
        goToNotificationFragment()
    }

    protected open fun clickToolbarProfile() {
        goToProfileFragment()
    }

    protected open fun goToNotificationFragment() = Unit

    protected open fun goToProfileFragment() = Unit

    fun updateShowToolbarBack(showBack: Boolean) {
        if (showBack) {
            ibToolbarBack?.visible()
        } else {
            ibToolbarBack?.gone()
        }
    }

    fun updateShowToolbarProfile(showProfile: Boolean) {
        if (showProfile) {
            ibToolbarProfile?.visible()
        } else {
            ibToolbarProfile?.gone()
        }
    }

    fun updateShowToolbarNotification(showNotification: Boolean) {
        if (showNotification) {
            ibToolbarNotification?.visible()
        } else {
            ibToolbarNotification?.gone()
        }
    }

    fun hideToolbar() {
        clToolbar?.gone()
    }

    private fun hideAllElementToolbar() {
        ibToolbarBack?.gone()
        tvToolbarTitle?.gone()
        ibToolbarProfile?.gone()
        ibToolbarNotification?.gone()
    }

    private fun showToolbarLayout() {
        clToolbar?.visible()
    }

    private fun hideToolbarLayout() {
        clToolbar?.gone()
    }

    fun fragmentFullScreenLayoutWithoutToolbar() {
        hideToolbarLayout()
    }

    fun fragmentLayoutWithToolbar() {
        showToolbarLayout()
    }

    fun updateShowToolbarTitle(title: String) {
        if (title.isNotBlank()) {
            tvToolbarTitle?.visible()
            tvToolbarTitle?.text = title
        } else {
            tvToolbarTitle?.gone()
        }
    }

    fun showMessageWithOneButton(
        iconID: Int,
        title: String? = null,
        message: String,
        textPositiveButton: String,
        listener: MessageDialogFragment.MessageDialogListener
    ) {
        if (supportFragmentManager.findFragmentByTag(MessageDialogFragment.MESSAGE_DIALOG_TAG) == null) {
            messageDialogFragment.apply {
                this.iconID = iconID
                this.title = title
                this.message = message
                this.positiveButton = textPositiveButton
                this.listener = listener
            }
            messageDialogFragment.show(
                supportFragmentManager,
                MessageDialogFragment.MESSAGE_DIALOG_TAG
            )
        } else {
            messageDialogFragment.refreshValues(
                iconID = iconID,
                title = title,
                message = message,
                positiveButton = textPositiveButton,
                listener = listener
            )
        }

    }

    fun showMessageWithTwoButton(
        iconID: Int,
        title: String? = null,
        message: String,
        textPositiveButton: String,
        textNegativeButton: String,
        listener: MessageDialogFragment.MessageDialogListener
    ) {
        if (supportFragmentManager.findFragmentByTag(MessageDialogFragment.MESSAGE_DIALOG_TAG) == null) {
            messageDialogFragment.apply {
                this.iconID = iconID
                this.title = title
                this.message = message
                this.positiveButton = textPositiveButton
                this.negativeButton = textNegativeButton
                this.listener = listener
            }
            messageDialogFragment.show(
                supportFragmentManager,
                MessageDialogFragment.MESSAGE_DIALOG_TAG
            )
        } else {
            messageDialogFragment.refreshValues(
                title = title,
                message = message,
                positiveButton = textPositiveButton,
                negativeButton = textNegativeButton,
                listener = listener
            )
        }
    }

    protected open fun callViewModelSaveData() = Unit
    abstract fun inflateBinding()
    abstract fun observeViewModel()
    abstract fun createAfterInflateBindingSetupObserverViewModel()
    abstract fun configureToolbarAndConfigScreenSections()
}