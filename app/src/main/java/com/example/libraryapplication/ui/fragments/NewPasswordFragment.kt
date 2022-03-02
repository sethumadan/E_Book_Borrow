package com.example.libraryapplication.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.libraryapplication.data.entities.User
import com.example.libraryapplication.databinding.FragmentNewPasswordBinding
import com.example.libraryapplication.ui.utils.hideInput
import com.example.libraryapplication.viewmodels.AuthenticationViewModel
import com.example.libraryapplication.viewmodels.response.Report


class NewPasswordFragment : DialogFragment() {
    private lateinit var authenticationViewModel: AuthenticationViewModel
    private lateinit var binding: FragmentNewPasswordBinding
    lateinit var user: User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeViewModels()
        user = authenticationViewModel.getUser()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewPasswordBinding.inflate(inflater, container, false)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        isCancelable = false
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onClickConfirm()
        onClickCancel()
        onSetFocusChangeListener()
    }

    private fun onClickCancel() {
        binding.newPasswordCancel.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun onSetFocusChangeListener() {
        binding.newPasswordPassword.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.newPasswordPasswordTopLayout.error = null
        }
        binding.newPasswordConfirmPassword.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.newPasswordConfirmPasswordTopLayout.error = null
        }
    }


    private fun onClickConfirm() {
        binding.newPasswordConfirm.setOnClickListener {
            binding.newPasswordConfirm.hideInput()
            cancelFocus()
            checkAndSetNewPassword()
        }
    }

    private fun checkAndSetNewPassword() {
        when {
            binding.newPasswordPassword.text.isNullOrEmpty() -> binding.newPasswordPasswordTopLayout.error =
                Report.SignupResponse.PASSWORD_EMPTY.reason
            !authenticationViewModel.isValidPassword(binding.newPasswordPassword.text.toString()) -> binding.newPasswordPasswordTopLayout.error =
                Report.SignupResponse.INVALID_PASSWORD.reason
            binding.newPasswordConfirmPassword.text.isNullOrEmpty() -> binding.newPasswordConfirmPasswordTopLayout.error =
                Report.SignupResponse.PASSWORD_EMPTY.reason
            binding.newPasswordPassword.text.toString() != binding.newPasswordConfirmPassword.text.toString() -> binding.newPasswordConfirmPasswordTopLayout.error =
                Report.SignupResponse.PASSWORD_CONFIRMATION_FAILED.reason
            else -> {
                changePasswordSuccess()
            }
        }
    }

    private fun changePasswordSuccess() {
        authenticationViewModel.updateUserPassword(
            binding.newPasswordPassword.text.toString(),
            user.id
        )
        findNavController().navigateUp()
        Toast.makeText(context, "Password Changed", Toast.LENGTH_SHORT).show()
    }

    private fun cancelFocus() {
        binding.newPasswordPassword.clearFocus()
        binding.newPasswordConfirmPassword.clearFocus()
    }

    private fun initializeViewModels() {
        authenticationViewModel =
            ViewModelProvider(requireActivity())[AuthenticationViewModel::class.java]
    }

}