package com.team2.chitchat.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.team2.chitchat.R
import com.team2.chitchat.data.domain.model.chats.GetChatModel
import com.team2.chitchat.databinding.FragmentChatBinding
import com.team2.chitchat.ui.base.BaseFragment
import com.team2.chitchat.ui.chat.adapter.ChatAdapter
import com.team2.chitchat.ui.extensions.invisible
import com.team2.chitchat.ui.extensions.toastLong
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChatFragment : BaseFragment<FragmentChatBinding>(), View.OnClickListener,
    ChatAdapter.ChatAdapterListener {

    private val chatViewModel: ChatViewModel by viewModels()
    private val chatAdapter = ChatAdapter(this)
    private val args: ChatFragmentArgs by navArgs()
    private lateinit var keyboardListener: ViewTreeObserver.OnGlobalLayoutListener

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
        setUpKeyboardListener()
        chatViewModel.getChat(args.idChat)
    }

    private fun configRecyclerView() {
        binding?.rvChat?.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = chatAdapter
        }
    }

    private fun setUpListeners() {
        binding?.ibBack?.setOnClickListener(this)
        binding?.ibSend?.setOnClickListener(this)
    }

    override fun configureToolbarAndConfigScreenSections() {
        hideToolbar()
    }

    override fun observeViewModel() {
        lifecycleScope.launch {
            chatViewModel.messagesStateFlow.collect { messages ->
                chatAdapter.submitListWithScroll(messages, binding?.rvChat)
            }
        }

        lifecycleScope.launch {
            chatViewModel.chatStateFlow.collect { chat ->
                getUser(chat)
            }
        }

        lifecycleScope.launch {
            chatViewModel.errorFlow.collect {
                requireContext().toastLong(it.error)
            }
        }
    }

    override fun viewCreatedAfterSetupObserverViewModel(view: View, savedInstanceState: Bundle?) {
        chatViewModel.getMessagesForChat(args.idChat)
    }

    private fun getUser(chat: GetChatModel) {
        binding?.tvUsername?.text = chat.name
        if (chat.online) {
            binding?.tvStatus?.text = "En línea"
        } else {
            binding?.tvStatus?.invisible()
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.ibBack -> {
                findNavController().navigateUp()
            }

            R.id.ibSend -> {
                if (binding?.etSend?.text.toString().isNotEmpty()) {
                    chatViewModel.postNewMessage(binding?.etSend?.text.toString(), args.idChat)
                }
                binding?.etSend?.text?.clear()
            }
        }
    }

    private fun setUpKeyboardListener() {
        keyboardListener = ViewTreeObserver.OnGlobalLayoutListener {
            val rect = android.graphics.Rect()
            val rootView = binding?.root ?: return@OnGlobalLayoutListener
            rootView.getWindowVisibleDisplayFrame(rect)
            val screenHeight = rootView.rootView.height
            val keypadHeight = screenHeight - rect.bottom
            if (keypadHeight > 150) {
                binding?.rvChat?.scrollToPosition(chatAdapter.itemCount - 1)
            }
        }
        binding?.root?.viewTreeObserver?.addOnGlobalLayoutListener(keyboardListener)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding?.root?.viewTreeObserver?.removeOnGlobalLayoutListener(keyboardListener)
        binding = null
    }

    override fun onItemClick(messageId: String) = Unit

}