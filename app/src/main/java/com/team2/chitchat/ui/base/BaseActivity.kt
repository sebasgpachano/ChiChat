package com.team2.chitchat.ui.base

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import androidx.navigation.NavController
import androidx.viewbinding.ViewBinding
import com.team2.chitchat.R
import com.team2.chitchat.ui.dialogfragment.LoadingDialogFragment
import com.team2.chitchat.ui.dialogfragment.LoadingDialogFragment.Companion.LOADING_DIALOG_FRAGMENT_TAG
import com.team2.chitchat.utils.gone
import com.team2.chitchat.utils.visible
import javax.inject.Inject

abstract class BaseActivity<B : ViewBinding> : AppCompatActivity(), View.OnClickListener {
    lateinit var binding: B
    lateinit var navController: NavController

    private var clToolbar: ConstraintLayout? = null
    private var tbToolbar: Toolbar? = null
    private var ibToolbarBack: ImageButton? = null
    private var tvToolbarTitle: TextView? = null

    private var loadingDialogFragment: LoadingDialogFragment = LoadingDialogFragment()

    @Inject
    lateinit var baseActivityControlShowLoading: BaseActivityControlShowLoading
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
        createAfterInflateBindingSetupObserverViewModel(savedInstanceState)
        setListenersClickToolbarButtons()
    }

    fun showLoading(show: Boolean) {
        if (show) {
            if (baseActivityControlShowLoading.canShowLoading(
                    supportFragmentManager,
                    LOADING_DIALOG_FRAGMENT_TAG
                )
            ) {
                loadingDialogFragment.show(supportFragmentManager, LOADING_DIALOG_FRAGMENT_TAG)
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
    }

    fun showToolbar(
        showBack: Boolean = false,
        title: String = "",
    ) {
        var maxIconLeft = 0

        hideAllElementToolbar()
        tbToolbar?.visible()
        if (showBack) {
            maxIconLeft++
            ibToolbarBack?.visible()
        }
        if (title.isNotBlank()) {
            tvToolbarTitle?.visible()
            tvToolbarTitle?.text = title
            configMarginTitle(maxIconLeft)
        }
    }

    private fun configMarginTitle(numberIconsLeftRightTitle: Int) {
        if (tvToolbarTitle != null) {
            tvToolbarTitle!!.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                when (numberIconsLeftRightTitle) {
                    0 -> {
                        marginStart =
                            resources.getDimension(R.dimen.toolbar_margin_title_zero_buttons)
                                .toInt()
                        marginEnd =
                            resources.getDimension(R.dimen.toolbar_margin_title_zero_buttons)
                                .toInt()
                    }

                    else -> {
                        marginStart =
                            resources.getDimension(R.dimen.toolbar_margin_title_one_buttons).toInt()
                        marginEnd =
                            resources.getDimension(R.dimen.toolbar_margin_title_one_buttons).toInt()
                    }
                }
            }
        }
    }

    private fun setListenersClickToolbarButtons() {
        ibToolbarBack?.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.ibToolbarBack -> clickToolbarBack()
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount == 1) {
            finish()
        } else {
            super.onBackPressed()
        }
    }

    protected open fun clickToolbarBack() {
        onBackPressed()
    }

    fun updateShowToolbarBack(showBack: Boolean) {
        if (showBack) {
            ibToolbarBack?.visible()
        } else {
            ibToolbarBack?.gone()
        }
    }

    fun hideToolbar() {
        tbToolbar?.gone()
    }

    private fun hideAllElementToolbar() {
        ibToolbarBack?.gone()
        tvToolbarTitle?.gone()
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

    protected open fun callViewModelSaveData() = Unit
    abstract fun inflateBinding()
    abstract fun observeViewModel()
    abstract fun createAfterInflateBindingSetupObserverViewModel(savedInstanceState: Bundle?)
    abstract fun configureToolbarAndConfigScreenSections()
}