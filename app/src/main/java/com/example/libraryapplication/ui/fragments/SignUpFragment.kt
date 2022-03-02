package com.example.libraryapplication.ui.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.libraryapplication.R
import com.example.libraryapplication.databinding.FragmentSignUpBinding
import com.example.libraryapplication.safenavigation.safeNavigate
import com.example.libraryapplication.ui.utils.hideInput
import com.example.libraryapplication.viewmodels.AuthenticationViewModel
import com.example.libraryapplication.viewmodels.response.Report
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignUpFragment : Fragment() {
    private lateinit var binding: FragmentSignUpBinding
    private lateinit var authenticationViewModel: AuthenticationViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeViewModels()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sign_up, container, false)
        return binding.root
    }

    private fun initializeViewModels() {
        authenticationViewModel =
            ViewModelProvider(requireActivity())[AuthenticationViewModel::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setDistrictsAdapter()
        setUpFocusChangeListeners()
        maintainKeyBoard()
        onClickSignUp()
    }

    private fun setDistrictsAdapter() {
        val districts = resources.getStringArray(R.array.districts)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.subject_drop_down, districts)
        binding.district.setAdapter(arrayAdapter)
    }

    private fun onClickSignUp() {
        binding.signUp.setOnClickListener {
            clearFocus()
            binding.signUp.hideInput()
            signUp()
        }
    }

    private fun signUp() {
        lifecycleScope.launch {
            val result = authenticationViewModel.signUpCheck(
                binding.userName.text!!.toString(),
                binding.mobileNo.text!!.toString(),
                binding.password.text!!.toString(),
                binding.confirmPassword.text!!.toString(),
                binding.pinCode.text!!.toString(),
                binding.doorNo.text!!.toString(),
                binding.district.text!!.toString(),
                binding.landMark.text!!.toString(),
                binding.street.text!!.toString()
            )
            when (result) {
                Report.SignupResponse.UN_FILLED_ROWS -> setUnfilledRowsResponse()
                Report.SignupResponse.MOBILE_NO_ALREADY_TAKEN -> binding.mobileNoTopLayout.error =
                    result.reason
                Report.SignupResponse.INVALID_MOBILE_NO -> binding.mobileNoTopLayout.error =
                    result.reason
                Report.SignupResponse.INVALID_PASSWORD -> binding.passwordTopLayout.error =
                    result.reason
                Report.SignupResponse.PASSWORD_CONFIRMATION_FAILED -> binding.confirmPasswordTopLayout.error =
                    result.reason
                Report.SignupResponse.INVALID_DOOR_NO -> binding.doorNoTopLayout.error =
                    result.reason
                Report.SignupResponse.LAND_MARK_EMPTY -> binding.landMarkTopLayout.error=result.reason
                Report.SignupResponse.INVALID_PIN_CODE -> binding.pinCodeTopLayout.error =
                    result.reason
                else -> setSignUpSuccess()
            }
        }
    }

    private fun clearFocus() {
        binding.userName.clearFocus()
        binding.mobileNo.clearFocus()
        binding.password.clearFocus()
        binding.confirmPassword.clearFocus()
        binding.pinCode.clearFocus()
        binding.doorNo.clearFocus()
        binding.district.clearFocus()
    }

    private fun setSignUpSuccess() {
        val sharedPrefLogin =
            requireActivity().getSharedPreferences("Login", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPrefLogin?.edit()!!
        editor.putBoolean("loggedIn", true)
        editor.putInt("userId", authenticationViewModel.getUser().id)
        editor.apply()
        findNavController().safeNavigate(SignUpFragmentDirections.actionSignUpFragmentToHome2())
    }

    private fun setUnfilledRowsResponse() {
        setUnfilledUserNameText()
        setUnfilledMobileNoText()
        setUnfilledPasswordText()
        setUnfilledConfirmPasswordText()
        setUnfilledPinCodeText()
        setUnfilledDoorNoText()
        setUnfilledDistrictText()
        setUnfilledLandMarkText()
        setUnfilledStreetText()
    }

    private fun setUnfilledStreetText() {
        if (binding.street.text!!.isEmpty()) binding.streetTopLayout.error =
            Report.SignupResponse.STREET_NAME_EMPTY.reason
        else binding.streetTopLayout.error = null
    }

    private fun setUnfilledLandMarkText() {
        if (binding.landMark.text!!.isEmpty()) binding.landMarkTopLayout.error =
            Report.SignupResponse.LAND_MARK_EMPTY.reason
        else binding.landMarkTopLayout.error = null
    }

    private fun setUnfilledDistrictText() {
        if (binding.district.text!!.isEmpty()) binding.districtTopLayout.error =
            Report.SignupResponse.DISTRICT_EMPTY.reason
        else binding.districtTopLayout.error = null
    }

    private fun setUnfilledDoorNoText() {
        if (binding.doorNo.text!!.isEmpty()) binding.doorNoTopLayout.error =
            Report.SignupResponse.DOOR_NO_EMPTY.reason
        else binding.doorNoTopLayout.error = null
    }

    private fun setUnfilledPinCodeText() {
        when {
            binding.pinCode.text!!.isEmpty() -> binding.pinCodeTopLayout.error =
                Report.SignupResponse.PIN_CODE_EMPTY.reason
            else -> binding.pinCodeTopLayout.error = null
        }
    }

    private fun setUnfilledConfirmPasswordText() {
        if (binding.confirmPassword.text!!.isEmpty()) binding.confirmPasswordTopLayout.error =
            Report.SignupResponse.PASSWORD_EMPTY.reason
        else binding.confirmPasswordTopLayout.error = null
    }

    private fun setUnfilledPasswordText() {
        if (binding.password.text!!.isEmpty()) binding.passwordTopLayout.error =
            Report.SignupResponse.PASSWORD_EMPTY.reason
        else binding.passwordTopLayout.error = null
    }

    private fun setUnfilledMobileNoText() {
        if (binding.mobileNo.text!!.isEmpty()) binding.mobileNoTopLayout.error =
            Report.SignupResponse.MOBILE_EMPTY.reason
        else binding.mobileNoTopLayout.error = null
    }

    private fun setUnfilledUserNameText() {
        if (binding.userName.text!!.isEmpty()) binding.userNameTopLayout.error =
            Report.SignupResponse.NAME_EMPTY.reason
        else binding.userNameTopLayout.error = null
    }

    private fun maintainKeyBoard() {
        binding.pinCode.setOnEditorActionListener { v, actionId, _ ->
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
    
    private fun setUpFocusChangeListeners() {
        binding.userName.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.userNameTopLayout.error = null
        }
        binding.mobileNo.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.mobileNoTopLayout.error = null
        }
        binding.password.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.passwordTopLayout.error = null
        }
        binding.confirmPassword.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.confirmPasswordTopLayout.error = null
        }
        binding.pinCode.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.pinCodeTopLayout.error = null
        }
        binding.doorNo.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.doorNoTopLayout.error = null
        }
        binding.district.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.districtTopLayout.error = null
        }
    }
}
