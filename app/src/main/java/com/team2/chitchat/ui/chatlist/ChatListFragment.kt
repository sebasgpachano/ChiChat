package com.team2.chitchat.ui.chatlist

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.security.crypto.EncryptedSharedPreferences
import com.team2.chitchat.R
import com.team2.chitchat.data.domain.model.chats.ListChatsModel
import com.team2.chitchat.data.repository.preferences.EncryptedSharedPreferencesKeys.Companion.ENCRYPTED_SHARED_PREFERENCES_KEY_PROFILE_IMAGE
import com.team2.chitchat.data.session.DataUserSession
import com.team2.chitchat.databinding.FragmentChatListBinding
import com.team2.chitchat.ui.base.BaseFragment
import com.team2.chitchat.ui.chatlist.adapter.ChatsListAdapter
import com.team2.chitchat.ui.dialogfragment.MessageDialogFragment
import com.team2.chitchat.ui.extensions.TAG
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ChatListFragment : BaseFragment<FragmentChatListBinding>(),
    ChatsListAdapter.ListChatsAdapterListener, View.OnClickListener {
    private val chatListViewModel: ChatListViewModel by viewModels()
    private val chatsListAdapter = ChatsListAdapter(this)
    private var isDialogShowing = false
    private var lastRemovedChatId: String? = null
    private var allChats = ArrayList<ListChatsModel>()

    @Inject
    lateinit var dataUserSession: DataUserSession

    @Inject
    lateinit var encryptedSharedPreferences: EncryptedSharedPreferences

    override fun inflateBinding() {
        binding = FragmentChatListBinding.inflate(layoutInflater)
    }

    override fun createViewAfterInflateBinding(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ) {
        if (!dataUserSession.haveSession()) {
            findNavController().navigate(R.id.action_chatListFragment_to_loginNavigation)
        }
        setImage()
        configRecyclerView()
        setupListeners()
        setupSwipeToDelete()
        setupSearch()
    }

    private fun setImage() {
        val bitmap: Bitmap
        val imageBase64 = encryptedSharedPreferences.getString(
            ENCRYPTED_SHARED_PREFERENCES_KEY_PROFILE_IMAGE,
            null
        )
        if (imageBase64.isNullOrBlank()) {
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.baseline_person_24)
        } else {
            val imageBytes = Base64.decode(imageBase64, Base64.DEFAULT)
            bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        }
        updateProfileImage(bitmap)
    }

    private fun configRecyclerView() {
        binding?.rvChatList?.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = chatsListAdapter
        }
    }

    private fun setupListeners() {
        binding?.btnAddChat?.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btnAddChat -> findNavController().navigate(ChatListFragmentDirections.actionChatListFragmentToContactsListFragment())
        }
    }

    private fun setupSearch() {
        binding?.etSearchUser?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) =
                Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

            override fun afterTextChanged(s: Editable?) {
                filterUsers(s.toString())
            }
        })
    }

    private fun filterUsers(query: String) {
        val filteredList = allChats.filter { it.name.contains(query, ignoreCase = true) }
        updateList(ArrayList(filteredList))
    }

    private fun setupSwipeToDelete() {
        val itemTouchHelper = ItemTouchHelper(object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val chat = chatsListAdapter.getItemSelected(position)
                if (chat.notification > 0 && !isDialogShowing) {
                    isDialogShowing = true
                    showErrorMessage(
                        message = requireContext().getString(R.string.chat_list_warning_notification_chat),
                        listener = object : MessageDialogFragment.MessageDialogListener {
                            override fun positiveButtonOnclick(view: View) {
                                isDialogShowing = false
                                refreshFragment()
                            }
                        }
                    )
                } else {
                    lastRemovedChatId = chat.id
                    chatListViewModel.deleteChat(chat.id)
                }
            }
        })
        itemTouchHelper.attachToRecyclerView(binding?.rvChatList)
    }

    override fun configureToolbarAndConfigScreenSections() {
        fragmentLayoutWithToolbar()
        showToolbar(title = getString(R.string.chat_list_title_toolbar), showProfile = true)
    }

    override fun observeViewModel() {
        lifecycleScope.launch {
            chatListViewModel.loadingFlow.collect { loading ->
                showLoading(loading)
            }
        }
        lifecycleScope.launch {
            chatListViewModel.errorFlow.collect { errorModel ->
                when (errorModel.errorCode) {
                    "401" -> {
                        if (!isDialogShowing) {
                            isDialogShowing = true
                            showMessageDialog(
                                iconID = R.drawable.delete_chat_icon,
                                title = requireContext().getString(R.string.chat_list_delete_chat),
                                message = requireContext().getString(R.string.chat_list_delete_chat_question),
                                textPositiveButton = requireContext().getString(R.string.accept),
                                textNegativeButton = requireContext().getString(R.string.cancel),
                                listener = object : MessageDialogFragment.MessageDialogListener {
                                    override fun positiveButtonOnclick(view: View) {
                                        chatListViewModel.updateChatView(
                                            lastRemovedChatId ?: "",
                                            view = false
                                        )
                                        isDialogShowing = false
                                    }

                                    override fun negativeButtonOnclick(view: View) {
                                        isDialogShowing = false
                                        refreshFragment()
                                    }
                                }
                            )
                        }
                    }

                    "403" -> {
                        Log.d(TAG, "%> token error")
                    }
                }

            }
        }
        lifecycleScope.launch {
            chatListViewModel.chatsSharedFlow.collect { chatsList ->
                allChats = chatsList
                updateList(chatsList)
            }
        }

        lifecycleScope.launch {
            chatListViewModel.deleteChatSharedFlow.collect {
            }
        }
    }

    private fun refreshFragment() {
        val fragmentTransaction = parentFragmentManager.beginTransaction()
        fragmentTransaction.detach(this).commitNow()
        parentFragmentManager.beginTransaction().attach(this).commitNow()
    }

    private fun updateList(chatList: ArrayList<ListChatsModel>) {
        chatsListAdapter.submitList(chatList) {
            binding?.rvChatList?.scrollToPosition(0)
        }
    }

    override fun viewCreatedAfterSetupObserverViewModel(view: View, savedInstanceState: Bundle?) {
        chatListViewModel.getChats()
    }

    override fun onItemClick(idChat: String) {
        findNavController().navigate(
            ChatListFragmentDirections.actionChatListFragmentToChatFragment(idChat)
        )
    }
}