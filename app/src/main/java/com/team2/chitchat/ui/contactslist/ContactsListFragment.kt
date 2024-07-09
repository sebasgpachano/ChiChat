package com.team2.chitchat.ui.contactslist

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.team2.chitchat.R
import com.team2.chitchat.data.domain.model.users.GetUserModel
import com.team2.chitchat.databinding.FragmentContactsListBinding
import com.team2.chitchat.ui.base.BaseFragment
import com.team2.chitchat.ui.contactslist.adapter.ContactsListAdapter
import com.team2.chitchat.ui.extensions.TAG
import com.team2.chitchat.ui.extensions.toastLong
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ContactsListFragment : BaseFragment<FragmentContactsListBinding>(),
    ContactsListAdapter.ContactsListAdapterListener {
    private val contactsListViewModel: ContactsListViewModel by viewModels()
    private val contactsListAdapter = ContactsListAdapter(this)
    override fun inflateBinding() {
        binding = FragmentContactsListBinding.inflate(layoutInflater)
    }

    override fun createViewAfterInflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) {
        configRecyclerView()
        configSwipeRefreshLayout()
    }

    private fun configRecyclerView() {
        binding?.rvContacts?.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = contactsListAdapter
        }
    }

    private fun configSwipeRefreshLayout() {
        binding?.srContacts?.setOnRefreshListener {
            contactsListViewModel.getContactsList()
        }
    }

    override fun configureToolbarAndConfigScreenSections() {
        fragmentLayoutWithToolbar()
        showToolbar(title = getString(R.string.list_contacts_title_toolbar), showBack = true)
    }

    override fun observeViewModel() {
        lifecycleScope.launch {
            contactsListViewModel.loadingFlow.collect { loading ->
                showLoading(loading)
            }
        }
        lifecycleScope.launch {
            contactsListViewModel.errorFlow.collect { errorModel ->
                if (errorModel.message == "No token provided") {
                    requireContext().toastLong(getString(R.string.apy_error_session_expired))
                    findNavController().navigateUp()
                }
            }
        }
        lifecycleScope.launch {
            contactsListViewModel.contactsSharedFlow.collect { contactsList ->
                updateList(contactsList)
            }
        }
    }

    private fun updateList(contactsList: ArrayList<GetUserModel>) {
        contactsListAdapter.submitList(contactsList) {
            binding?.rvContacts?.scrollToPosition(0)
        }
        binding?.srContacts?.isRefreshing = false
    }

    override fun viewCreatedAfterSetupObserverViewModel(view: View, savedInstanceState: Bundle?) {
        contactsListViewModel.getContactsList()
    }

    override fun onItemClick(idChat: String) {
        Log.d(TAG, "%> Has pulsado en el chat con id: $idChat")
    }

}