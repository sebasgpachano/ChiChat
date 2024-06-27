package com.team2.chitchat.ui.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.team2.chitchat.R
import com.team2.chitchat.databinding.FragmentProfileBinding
import com.team2.chitchat.ui.base.BaseFragment


/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : BaseFragment<FragmentProfileBinding>() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun inflateBinding() {
        binding = FragmentProfileBinding.inflate(layoutInflater)
    }

    override fun createViewAfterInflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) {

    }

    override fun configureToolbarAndConfigScreenSections() {
        fragmentLayoutWithToolbar()
    }

    override fun observeViewModel() {
    }

    override fun viewCreatedAfterSetupObserverViewModel(view: View, savedInstanceState: Bundle?) {
    }
    private fun initializeListeners() {

    }
}