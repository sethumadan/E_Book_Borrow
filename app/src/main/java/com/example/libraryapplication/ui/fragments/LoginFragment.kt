package com.example.libraryapplication.ui.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.libraryapplication.R
import com.example.libraryapplication.databinding.FragmentLoginBinding
import com.example.libraryapplication.safenavigation.safeNavigate
import com.example.libraryapplication.ui.utils.hideInput
import com.example.libraryapplication.viewmodels.AuthenticationViewModel
import com.example.libraryapplication.viewmodels.response.Report
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var authenticationViewModel: AuthenticationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeViewModels()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        manageKeyBoard()
        onClickCreateNewAccount()
        onClickLogin()
        setUpFocusChangeListeners()
    }

    private fun onClickLogin() {
        binding.login.setOnClickListener {
            binding.login.hideInput()
            login()
        }
    }

    private fun login() {
        lifecycleScope.launch {
            when (authenticationViewModel.loginCheck(
                binding.mobileNoEditText.text.toString(),
                binding.passwordEditText.text.toString()
            )) {
                Report.LoginResponse.SUCCESS -> {
                    setLoginSuccess()
                }
                Report.LoginResponse.INVALID_MOBILE_NO -> binding.loginMobileNoTopLayout.error = "Invalid Mobile No"
                else -> binding.loginPasswordTopLayout.error = "Invalid Password"
            }
        }
    }

    private fun setLoginSuccess() {
        val sharedPrefLogin = requireActivity().getSharedPreferences(
            "Login",
            Context.MODE_PRIVATE
        )
        val editor: SharedPreferences.Editor = sharedPrefLogin?.edit()!!
        editor.putBoolean("loggedIn", true)
        editor.putInt("userId", authenticationViewModel.getUser().id)
        editor.apply()
        findNavController().safeNavigate(LoginFragmentDirections.actionLoginFragmentToHome2())
    }

    private fun onClickCreateNewAccount() {
        binding.createNewAccount.setOnClickListener {
            findNavController().safeNavigate(LoginFragmentDirections.actionLoginFragmentToSignUpFragment())
        }
    }

    private fun setUpFocusChangeListeners() {
        binding.mobileNoEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.loginMobileNoTopLayout.error = null
        }
        binding.passwordEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.loginPasswordTopLayout.error = null
        }
    }

    private fun initializeViewModels() {
        authenticationViewModel =
            ViewModelProvider(requireActivity())[AuthenticationViewModel::class.java]
    }

    private fun manageKeyBoard() {
        binding.passwordEditText.setOnEditorActionListener { v, actionId, _ ->
            return@setOnEditorActionListener when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    v.clearFocus()
                    v.hideInput()
                    true
                }
                else -> false
            }
        }
    }

}
