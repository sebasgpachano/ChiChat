package com.team2.chitchat.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.team2.chitchat.R
import com.team2.chitchat.databinding.FragmentChatBinding
import com.team2.chitchat.ui.base.BaseFragment
import com.team2.chitchat.ui.chat.adapter.ChatAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChatFragment : BaseFragment<FragmentChatBinding>(), View.OnClickListener,
    ChatAdapter.ChatAdapterListener {

    private val chatViewModel: ChatViewModel by viewModels()
    private val chatAdapter = ChatAdapter(this)
    private val args: ChatFragmentArgs by navArgs()

    override fun inflateBinding() {
        binding = FragmentChatBinding.inflate(layoutInflater)
    }

    override fun createViewAfterInflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) {
        setUpListeners()
        configRecyclerView()
    }

    private fun configRecyclerView() {
        binding?.rvChat?.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = chatAdapter
        }
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
        lifecycleScope.launch {
            chatViewModel.messagesStateFlow.collect { messages ->
                chatAdapter.submitList(messages)
            }
        }
    }

    override fun viewCreatedAfterSetupObserverViewModel(view: View, savedInstanceState: Bundle?) {
        chatViewModel.getMessagesForChat(args.idChat)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.ibToolbarBack -> {
                findNavController().navigateUp()
            }

            R.id.ibProfile -> {
                findNavController().navigate(ChatFragmentDirections.actionChatFragmentToProfileFragment())
            }

            R.id.ibSend -> {
                //TODO sebas
            }
        }
    }

    override fun onItemClick(messageId: String) {
        TODO("Not yet implemented")
    }

}