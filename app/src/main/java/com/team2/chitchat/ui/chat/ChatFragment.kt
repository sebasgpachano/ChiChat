package com.team2.chitchat.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.navigateUp
import com.team2.chitchat.R
import com.team2.chitchat.databinding.FragmentChatBinding
import com.team2.chitchat.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatFragment : BaseFragment<FragmentChatBinding>(), View.OnClickListener {

    override fun inflateBinding() {
        binding = FragmentChatBinding.inflate(layoutInflater)
    }

    override fun createViewAfterInflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) {
        setUpListeners()
    }

    private fun setUpListeners() {
        binding?.ibToolbarBack?.setOnClickListener(this)
        binding?.ibProfile?.setOnClickListener(this)
        binding?.ibSend?.setOnClickListener(this)
    }

    override fun configureToolbarAndConfigScreenSections() {
        hideToolbar()
    }

    override fun observeViewModel() {
        //TODO("Not yet implemented")
    }

    override fun viewCreatedAfterSetupObserverViewModel(view: View, savedInstanceState: Bundle?) {
        //TODO("Not yet implemented")
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.ibToolbarBack -> {
                findNavController().navigateUp()
            }

            R.id.ibProfile -> {
                //TODO sebas
            }

            R.id.ibSend -> {
                //TODO sebas
            }
        }
    }

}