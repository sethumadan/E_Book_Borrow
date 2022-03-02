package com.example.libraryapplication.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.libraryapplication.databinding.FragmentNewMobileNoBinding
import com.example.libraryapplication.ui.utils.hideInput
import com.example.libraryapplication.viewmodels.AuthenticationViewModel
import com.example.libraryapplication.viewmodels.response.Report
import kotlinx.coroutines.launch

class NewMobileNoFragment : DialogFragment() {
    lateinit var binding: FragmentNewMobileNoBinding
    lateinit var authenticationViewModel: AuthenticationViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeViewModels()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewMobileNoBinding.inflate(inflater, container, false)
        initializeViewModels()
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        onClickConfirm()
        onClickCancel()
        isCancelable = false
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onSetFocusChangeListener()
    }

    private fun initializeViewModels() {
        authenticationViewModel =
            ViewModelProvider(requireActivity())[AuthenticationViewModel::class.java]
    }

    private fun onClickConfirm() {
        binding.newMobNoConfirm.setOnClickListener {
            cancelFocus()
            checkAndSetChangeMobileNo()
        }
    }

    private fun checkAndSetChangeMobileNo() {
        lifecycleScope.launch {
            binding.newMobNoConfirm.hideInput()
            if (!authenticationViewModel.isValidMobileNo(binding.newMobNo.text.toString())) {
                binding.newMobNoTopLayout.error = Report.SignupResponse.INVALID_MOBILE_NO.reason
            } else if (authenticationViewModel.isMobileAlreadyExists(binding.newMobNo.text.toString())) {
                binding.newMobNoTopLayout.error =
                    Report.SignupResponse.MOBILE_NO_ALREADY_TAKEN.reason
            } else {
                mobileNoChangeSuccess()
            }
        }
    }

    private fun mobileNoChangeSuccess() {
        Toast.makeText(
            requireContext(),
            "Mobile No Changed Successfully",
            Toast.LENGTH_SHORT
        ).show()
        authenticationViewModel.updateUserMobileNo(
            binding.newMobNo.text.toString(),
            authenticationViewModel.getUser().id
        )
        findNavController().navigateUp()
    }

    private fun onClickCancel() {
        binding.newMobNoCancel.setOnClickListener {
            binding.newMobNoConfirm
            findNavController().navigateUp()
        }
    }

    private fun cancelFocus() {
        binding.newMobNo.clearFocus()
    }

    private fun onSetFocusChangeListener() {
        binding.newMobNo.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.newMobNoTopLayout.error = null
        }
    }
}