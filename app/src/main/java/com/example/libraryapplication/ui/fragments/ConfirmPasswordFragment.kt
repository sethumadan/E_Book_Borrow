package com.example.libraryapplication.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.libraryapplication.R
import com.example.libraryapplication.data.entities.User
import com.example.libraryapplication.databinding.FragmentComfirmPasswordBinding
import com.example.libraryapplication.safenavigation.safeNavigate
import com.example.libraryapplication.ui.fragments.args.ChangeType
import com.example.libraryapplication.ui.utils.hideInput
import com.example.libraryapplication.viewmodels.AuthenticationViewModel
import kotlinx.coroutines.launch


class ConfirmPasswordFragment : DialogFragment() {
    private lateinit var args: ConfirmPasswordFragmentArgs
    private lateinit var authenticationViewModel: AuthenticationViewModel
    private lateinit var binding: FragmentComfirmPasswordBinding
   private lateinit var user: User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeViewModels()
        args = ConfirmPasswordFragmentArgs.fromBundle(requireArguments())
        user = authenticationViewModel.getUser()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_comfirm_password, container, false)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onSetFocusChangeListener()
        onClickConfirm()
        onClickCancel()

    }
    private fun onSetFocusChangeListener() {
        binding.confirmPasswordPassword.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.confirmPasswordPasswordTopLayout.error = null
        }
    }

    private fun initializeViewModels() {
        authenticationViewModel =
            ViewModelProvider(requireActivity())[AuthenticationViewModel::class.java]
    }

    private fun onClickConfirm() {
        binding.confirmPasswordConfirmButton.setOnClickListener {
            binding.confirmPasswordConfirmButton.hideInput()
            binding.confirmPasswordPasswordTopLayout.clearFocus()
            passwordValidation()
        }
    }

    private fun passwordValidation() {
        lifecycleScope.launch {
            if (authenticationViewModel.isValidPassword(
                    binding.confirmPasswordPassword.text.toString(),
                    user.id
                )
            ) {
                setNavDestination()
            }
            binding.confirmPasswordPasswordTopLayout.error = "Password Mismatch"
        }
    }

    private fun setNavDestination() {
        if (args.changeType == ChangeType.PASSWORD) {
            findNavController().safeNavigate(
                ConfirmPasswordFragmentDirections.actionConfirmPasswordFragmentToNewPasswordFragment()
            )
        } else {
            findNavController().safeNavigate(ConfirmPasswordFragmentDirections.actionConfirmPasswordFragmentToNewMobileNoFragment())
        }
    }

    private fun onClickCancel() {
        binding.confirmPasswordCancelButton.setOnClickListener {
            binding.confirmPasswordConfirmButton.hideInput()
            binding.confirmPasswordPasswordTopLayout.clearFocus()
            findNavController().navigateUp()
        }
    }

}