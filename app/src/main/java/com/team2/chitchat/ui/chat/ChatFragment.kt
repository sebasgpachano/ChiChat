package com.team2.chitchat.ui.chat

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.inputmethod.EditorInfo
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
import com.team2.chitchat.ui.extensions.TAG
import com.team2.chitchat.ui.extensions.invisible
import com.team2.chitchat.ui.extensions.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ChatFragment : BaseFragment<FragmentChatBinding>(), View.OnClickListener,
    ChatAdapter.ChatAdapterListener {

    private val chatViewModel: ChatViewModel by viewModels()

    @Inject
    lateinit var chatAdapter: ChatAdapter
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
        newLine()
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
        chatAdapter.setListener(this)


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
                Log.d(TAG, "Error: ${it.message}")
            }
        }
    }

    override fun viewCreatedAfterSetupObserverViewModel(view: View, savedInstanceState: Bundle?) {
        chatViewModel.getMessagesForChat(args.idChat)
    }

    private fun getUser(chat: GetChatModel) {
        binding?.tvUsername?.text = chat.name
        if (chat.online) {
            binding?.tvStatus?.visible()
            binding?.tvStatus?.text = getString(R.string.online)
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
                val rawMessage = binding?.etSend?.text.toString()

                val trimmedMessage = rawMessage.lines()
                    .filter { it.isNotBlank() }
                    .joinToString("\n")

                if (trimmedMessage.isNotEmpty()) {
                    chatViewModel.postNewMessage(trimmedMessage, args.idChat)
                    binding?.etSend?.text?.clear()
                }
            }
        }
    }

    private fun setUpKeyboardListener() {
        keyboardListener = ViewTreeObserver.OnGlobalLayoutListener {
            val rootView = binding?.root ?: return@OnGlobalLayoutListener
            val rect = android.graphics.Rect()
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

    override fun onItemClick() = Unit

    private fun newLine() {
        binding?.etSend?.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
                val editText = binding?.etSend
                editText?.let {
                    val start = it.selectionStart
                    val end = it.selectionEnd
                    it.text?.replace(start, end, "\n")
                }
                return@setOnEditorActionListener true
            }
            false
        }
    }
}