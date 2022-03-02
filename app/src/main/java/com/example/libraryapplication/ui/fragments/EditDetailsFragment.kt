package com.example.libraryapplication.ui.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.libraryapplication.R
import com.example.libraryapplication.data.entities.User
import com.example.libraryapplication.databinding.FragmentEditDetailsBinding
import com.example.libraryapplication.safenavigation.safeNavigate
import com.example.libraryapplication.ui.adapter.DistrictsAdapter
import com.example.libraryapplication.ui.fragments.args.ChangeType
import com.example.libraryapplication.ui.utils.hideInput
import com.example.libraryapplication.viewmodels.AuthenticationViewModel
import com.example.libraryapplication.viewmodels.response.Report

class EditDetailsFragment : Fragment(), DistrictsAdapter.ClickListener {
    enum class Type {
        NAME, DISTRICT, DOOR_NO, PIN_CODE, LAND_MARK, STREET_NAME
    }

    private lateinit var binding: FragmentEditDetailsBinding
    private lateinit var authenticationViewModel: AuthenticationViewModel
    private lateinit var user: User
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DistrictsAdapter
    private lateinit var defaultName: String
    private lateinit var defaultPinCode: String
    private lateinit var defaultDoorNo: String
    private lateinit var defaultDistrict: String
    private lateinit var defaultLandMark: String
    private lateinit var defaultStreetName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeViewModels()
        user = authenticationViewModel.getUser()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_edit_details, container, false)
        return binding.root
    }

    private fun initializeViewModels() {
        authenticationViewModel =
            ViewModelProvider(requireActivity())[AuthenticationViewModel::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setDistrictsRecyclerView()
        setUserDetails()
        setUpFocusChangeListeners()
        onClickUpdate()
        onClickChangePassword()
        onClickMobileNoChange()
        onClickPersonalDetailsCard()
        onClickEditDetails()
    }

    private fun onClickEditDetails() {
        binding.districtEditDetails.setOnClickListener {
            binding.editDetailsRecycler.visibility = View.GONE
            binding.districtEditDetails.clearFocus()
            if (adapter.defaultDistrict != binding.districtEditDetails.text.toString())
                showUpdateButton()
            else {
                hideUpdateButton()
            }
        }
    }

    private fun updatedDetails(
        name: String,
        doorNo: String,
        pinCode: String,
        district: String,
        landMark: String,
        streetName: String
    ) {
        defaultName = name
        defaultDoorNo = doorNo
        defaultPinCode = pinCode
        defaultDistrict = district
        defaultLandMark = landMark
        defaultStreetName = streetName
    }


    private fun setDistrictsRecyclerView() {
        adapter = DistrictsAdapter()
        recyclerView = binding.editDetailsRecycler
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter.setClickListener(this)
        adapter.setData(resources.getStringArray(R.array.districts).toList())
    }

    private fun clearFocus() {
        binding.editDetailsName.clearFocus()
        binding.pincodeEditDetails.clearFocus()
        binding.districtEditDetails.clearFocus()
        binding.doorNoEditDetails.clearFocus()
        binding.landMarkEditDetails.clearFocus()
        binding.streetEditDetails.clearFocus()
    }

    private fun onClickMobileNoChange() {
        binding.changeMobileNoEdit.setOnClickListener {
            findNavController().safeNavigate(
                EditDetailsFragmentDirections.actionEditDetailsFragmentToConfirmPasswordFragment(
                    ChangeType.MOBILE_NO
                )
            )
        }
    }

    private fun onClickChangePassword() {
        binding.changePasswordEdit.setOnClickListener {
            findNavController().safeNavigate(
                EditDetailsFragmentDirections.actionEditDetailsFragmentToConfirmPasswordFragment(
                    ChangeType.PASSWORD
                )
            )
        }
    }

    private fun onClickUpdate() {
        binding.editDetailsUpdate.setOnClickListener {
            clearFocus()
            binding.personalDetailsCard.hideInput()
            hideUpdateButton()
            updatedDetails(
                binding.editDetailsName.text.toString(),
                binding.doorNoEditDetails.text.toString(),
                binding.pincodeEditDetails.toString(),
                binding.districtEditDetails.text.toString(),
                binding.landMarkEditDetails.text.toString(),
                binding.streetEditDetails.text.toString()
            )
            when {
                !isValidInput(
                    binding.editDetailsName.text.toString(),
                    binding.doorNoEditDetails.text.toString(),
                    binding.pincodeEditDetails.toString(),
                    binding.districtEditDetails.text.toString(),
                    binding.landMarkEditDetails.text.toString(),
                    binding.streetEditDetails.text.toString()
                ) -> {
                    Toast.makeText(context, "All fields should be filled", Toast.LENGTH_LONG).show()
                }
                !authenticationViewModel.isValidPinCode(binding.pincodeEditDetails.text.toString()) -> {
                    showUpdateButton()
                    setAddress()
                    clearFocus()
                    Toast.makeText(
                        context,
                        Report.SignupResponse.INVALID_PIN_CODE.reason,
                        Toast.LENGTH_LONG
                    ).show()
                }
                else -> {
                    updateSuccess()
                }
            }
        }
    }

    private fun updateSuccess() {
        authenticationViewModel.updateUserDetails(
            binding.editDetailsName.text.toString(),
            binding.doorNoEditDetails.text.toString(),
            binding.landMarkEditDetails.text.toString(),
            binding.districtEditDetails.text.toString(),
            binding.streetEditDetails.text.toString(),
            binding.pincodeEditDetails.text.toString(),
            user.id
        )
        Toast.makeText(context, "Updated Successfully", Toast.LENGTH_SHORT).show()
    }

    private fun isValidInput(
        name: String,
        doorNo: String,
        pinCode: String,
        district: String,
        landMark: String,
        streetName: String
    ): Boolean {
        return !(name.isEmpty() || district.isEmpty() || doorNo.isEmpty() || pinCode.isEmpty() || landMark.isEmpty() || streetName.isEmpty())
    }

    private fun setUserDetails() {
        setName()
        setAddress()
    }

    private fun setName() {
        authenticationViewModel.getUser(user.id).observe(viewLifecycleOwner) {
            binding.editDetailsName.setText(it.name)
            defaultName = it.name
        }
    }

    private fun showUpdateButton() {
        binding.editDetailsUpdate.visibility = View.VISIBLE
    }

    private fun hideUpdateButton() {
        binding.editDetailsUpdate.visibility = View.GONE
    }

    private fun setAddress() {
        authenticationViewModel.getUser(user.id).observe(viewLifecycleOwner) {
            binding.doorNoEditDetails.setText(it.address.doorNo)
            binding.landMarkEditDetails.setText(it.address.landMark)
            binding.streetEditDetails.setText(it.address.streetName)
            binding.districtEditDetails.setText(it.address.district)
            binding.pincodeEditDetails.setText(it.address.pinCode)
            defaultPinCode = it.address.pinCode
            defaultDistrict = it.address.district
            defaultDoorNo = it.address.doorNo
            defaultLandMark = it.address.landMark
            defaultStreetName = it.address.streetName
            adapter.setAdapterDefaultDistrict(defaultDistrict)
        }
    }

    private fun setUpFocusChangeListeners() {
        authenticationViewModel.getUser(user.id).observe(viewLifecycleOwner) {
            setUpEditTextFocusChangeListener(
                binding.editDetailsName,
                it.name,
                binding.editDetailsName.text.toString(),
                Type.NAME.toString()
            )
            setUpEditTextFocusChangeListener(
                binding.doorNoEditDetails,
                it.address.doorNo,
                binding.doorNoEditDetails.text.toString(),
                Type.DOOR_NO.toString()
            )
            setUpEditTextFocusChangeListener(
                binding.pincodeEditDetails,
                it.address.pinCode,
                binding.pincodeEditDetails.text.toString(),
                Type.PIN_CODE.toString()
            )
            setUpEditTextFocusChangeListener(
                binding.landMarkEditDetails,
                it.address.landMark,
                binding.landMarkEditDetails.text.toString(),
                Type.LAND_MARK.toString()
            )
            setUpEditTextFocusChangeListener(
                binding.streetEditDetails,
                it.address.streetName,
                binding.streetEditDetails.text.toString(),
                Type.STREET_NAME.toString()
            )
            setUpDistrictFocusChangeListener()
        }
    }

    private fun setEditDetailsTextChangeListener(
        editDetails: EditText,
        defaultString: String,
        type: String
    ) {
        editDetails.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?, start: Int, count: Int, after: Int
            ) {
                hideUpdateButton()
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                setUpdateButtonVisibility(defaultString, s.toString(), type)
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
    }

    private fun setUpdateButtonVisibility(
        defaultString: String,
        currentString: String,
        type: String
    ) {
        when (type) {
            Type.NAME.toString() -> {
                checkNameAndSetUpdateVisibility(defaultString, currentString)
            }
            Type.DISTRICT.toString() -> {
                checkDistrictAndSetUpdateVisibility(defaultString, currentString)
            }
            Type.DOOR_NO.toString() -> {
                checkDoorNoAndSetUpdateVisibility(defaultString, currentString)
            }
            Type.PIN_CODE.toString() -> {
                checkPinCodeAndSetUpdateVisibility(defaultString, currentString)
            }
            Type.LAND_MARK.toString() -> {
                checkLandMarkAndSetUpdateVisibility(defaultString, currentString)
            }
            Type.STREET_NAME.toString() -> {
                checkStreetNameAndSetUpdateVisibility(defaultString, currentString)
            }
        }
        if (!isValidInput(
                binding.editDetailsName.text.toString(),
                binding.doorNoEditDetails.text.toString(),
                binding.pincodeEditDetails.text.toString(),
                binding.districtEditDetails.text.toString(),
                binding.landMarkEditDetails.text.toString(),
                binding.streetEditDetails.text.toString()
            )
        ) {
            hideUpdateButton()
        }
    }

    private fun checkLandMarkAndSetUpdateVisibility(defaultString: String, currentString: String) {
        if (defaultString != currentString || defaultName != binding.editDetailsName.text.toString() ||
            defaultDistrict != binding.districtEditDetails.text.toString() ||
            defaultDoorNo != binding.doorNoEditDetails.text.toString() || defaultPinCode != binding.pincodeEditDetails.text.toString()
            || defaultStreetName != binding.streetEditDetails.text.toString()
        )
            showUpdateButton()
        else hideUpdateButton()
    }

    private fun checkStreetNameAndSetUpdateVisibility(
        defaultString: String,
        currentString: String
    ) {
        if (defaultString != currentString || defaultName != binding.editDetailsName.text.toString() ||
            defaultDistrict != binding.districtEditDetails.text.toString() ||
            defaultDoorNo != binding.doorNoEditDetails.text.toString() || defaultPinCode != binding.pincodeEditDetails.text.toString()
            || defaultLandMark != binding.landMarkEditDetails.text.toString()
        )
            showUpdateButton()
        else hideUpdateButton()
    }

    private fun checkPinCodeAndSetUpdateVisibility(defaultString: String, currentString: String) {
        if (defaultString != currentString || defaultName != binding.editDetailsName.text.toString() ||
            defaultDistrict != binding.districtEditDetails.text.toString() || defaultDoorNo != binding.doorNoEditDetails.text.toString()
            || defaultLandMark != binding.landMarkEditDetails.text.toString() || defaultStreetName != binding.streetEditDetails.text.toString()
        )
            showUpdateButton()
        else hideUpdateButton()
    }

    private fun checkDoorNoAndSetUpdateVisibility(defaultString: String, currentString: String) {
        if (defaultString != currentString || defaultName != binding.editDetailsName.text.toString() ||
            defaultDistrict != binding.districtEditDetails.text.toString() || defaultPinCode != binding.pincodeEditDetails.text.toString()
            || defaultLandMark != binding.landMarkEditDetails.text.toString() || defaultStreetName != binding.streetEditDetails.text.toString()

        )
            showUpdateButton()
        else hideUpdateButton()
    }


    private fun checkDistrictAndSetUpdateVisibility(defaultString: String, currentString: String) {
        if (defaultString != currentString || defaultName != binding.editDetailsName.text.toString() ||
            defaultDoorNo != binding.doorNoEditDetails.text.toString() || defaultPinCode != binding.pincodeEditDetails.text.toString()
            || defaultLandMark != binding.landMarkEditDetails.text.toString() || defaultStreetName != binding.streetEditDetails.text.toString()
        )
            showUpdateButton()
        else hideUpdateButton()
    }

    private fun checkNameAndSetUpdateVisibility(defaultString: String, currentString: String) {
        if (defaultString != currentString || defaultDistrict != binding.districtEditDetails.text.toString() ||
            defaultDoorNo != binding.doorNoEditDetails.text.toString() || defaultPinCode != binding.pincodeEditDetails.text.toString()
            || defaultLandMark != binding.landMarkEditDetails.text.toString() || defaultStreetName != binding.streetEditDetails.text.toString()
        )
            showUpdateButton()
        else hideUpdateButton()
    }

    private fun setUpEditTextFocusChangeListener(
        editDetails: EditText,
        defaultString: String,
        currentString: String,
        type: String
    ) {
        editDetails.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                setEditDetailsTextChangeListener(editDetails, defaultString, type)
                setUpdateButtonVisibility(
                    defaultString,
                    currentString,
                    type
                )
                hideRecyclerView()
                editDetails.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.primary_text
                    )
                )
            } else {
                hideUpdateButton()
                editDetails.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.hint_color
                    )
                )
            }
        }
    }

    private fun setUpDistrictFocusChangeListener() {
        binding.districtEditDetails.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                setUpdateButtonVisibility(
                    adapter.defaultDistrict,
                    binding.districtEditDetails.text.toString(),
                    Type.DISTRICT.toString()
                )
                binding.editDetailsRecycler.visibility = View.VISIBLE
                binding.editDetailsUpdate.visibility = View.GONE
                binding.districtEditDetails.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.primary_text
                    )
                )
            } else {
                hideUpdateButton()
                binding.districtEditDetails.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.hint_color
                    )
                )
            }

        }
    }

    private fun onClickPersonalDetailsCard() {
        binding.personalDetailsCard.setOnClickListener {
            clearFocus()
            hideRecyclerView()
            checkNameAndSetUpdateVisibility(defaultName, binding.editDetailsName.text.toString())
            checkDoorNoAndSetUpdateVisibility(
                defaultDoorNo,
                binding.doorNoEditDetails.text.toString()
            )
            checkLandMarkAndSetUpdateVisibility(
                defaultLandMark,
                binding.landMarkEditDetails.text.toString()
            )
            checkStreetNameAndSetUpdateVisibility(
                defaultStreetName,
                binding.streetEditDetails.text.toString()
            )
            checkDistrictAndSetUpdateVisibility(
                defaultDistrict,
                binding.districtEditDetails.text.toString()
            )
            checkPinCodeAndSetUpdateVisibility(
                defaultPinCode,
                binding.pincodeEditDetails.text.toString()
            )
            binding.personalDetailsCard.hideInput()
        }
    }

    private fun hideRecyclerView() {
        binding.editDetailsRecycler.visibility = View.GONE
    }


    private fun setUpdateButtonVisibility() {
        if (defaultDistrict != binding.districtEditDetails.text.toString()) {
            binding.editDetailsUpdate.visibility = View.VISIBLE
        } else {
            binding.editDetailsUpdate.visibility = View.GONE
        }
    }

    override fun onClickDistrict(district: String) {
        binding.editDetailsRecycler.visibility = View.GONE
        binding.districtEditDetails.setText(district)
        binding.districtEditDetails.clearFocus()
        setUpdateButtonVisibility()
    }
}
/*
    private fun setNameTextChangeListener() {
        binding.editDetailsName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                setUpdateButtonVisibility(defaultName, s.toString(), Type.NAME.toString())
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }

 private fun setDoorNoTextChangeListener() {
        binding.doorNoEditDetails.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?, start: Int, count: Int, after: Int
            ) {
                hideUpdateButton()
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                setUpdateButtonVisibility(defaultDoorNo, s.toString(), Type.DOOR_NO.toString())
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
    }

    private fun setStreetNameTextChangeListener() {
        binding.streetEditDetails.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                setUpdateButtonVisibility(
                    defaultStreetName,
                    s.toString(),
                    Type.STREET_NAME.toString()
                )
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })
    }

    private fun setLandMarkTextChangeListener() {
        binding.landMarkEditDetails.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                setUpdateButtonVisibility(defaultLandMark, s.toString(), Type.LAND_MARK.toString())
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

    }

    private fun setPinCodeTextChangeListener() {
        binding.pincodeEditDetails.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                setUpdateButtonVisibility(defaultPinCode, s.toString(), Type.PIN_CODE.toString())
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
    }
    private fun setUpDoorNoFocusChangeListener() {
        binding.doorNoEditDetails.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                setDoorNoTextChangeListener()
                setUpdateButtonVisibility(
                    defaultDoorNo,
                    binding.doorNoEditDetails.text.toString(),
                    Type.DOOR_NO.toString()
                )
                hideRecyclerView()
                binding.doorNoEditDetails.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.primary_text
                    )
                )
            } else {
                hideUpdateButton()
                binding.doorNoEditDetails.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.hint_color
                    )
                )
            }
        }
    }

   private fun setUpStreetNameFocusChangeListener() {
        binding.streetEditDetails.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                setStreetNameTextChangeListener()
                setUpdateButtonVisibility(
                    defaultStreetName,
                    binding.streetEditDetails.text.toString(),
                    Type.STREET_NAME.toString()
                )
                hideRecyclerView()
                binding.streetEditDetails.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.primary_text
                    )
                )
            } else {
                hideUpdateButton()
                binding.streetEditDetails.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.hint_color
                    )
                )
            }
        }
    }


 private fun setUpLandMarkFocusChangeListener() {
        binding.landMarkEditDetails.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                setLandMarkTextChangeListener()
                setUpdateButtonVisibility(
                    defaultLandMark,
                    binding.landMarkEditDetails.text.toString(),
                    Type.LAND_MARK.toString()
                )
                hideRecyclerView()
                binding.landMarkEditDetails.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.primary_text
                    )
                )
            } else {
                hideUpdateButton()
                binding.landMarkEditDetails.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.hint_color
                    )
                )
            }
        }
    }

    private fun setUpPinCodeFocusChangeListener() {
        binding.pincodeEditDetails.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                setPinCodeTextChangeListener()
                setUpdateButtonVisibility(
                    defaultPinCode,
                    binding.pincodeEditDetails.text.toString(),
                    Type.PIN_CODE.toString()
                )
                hideRecyclerView()
                binding.pincodeEditDetails.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.primary_text
                    )
                )
            } else {
                hideUpdateButton()
                binding.pincodeEditDetails.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.hint_color
                    )
                )
            }
        }
    }
private fun setUpNameFocusChangeListener() {
        binding.editDetailsName.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                setNameTextChangeListener()
                setUpdateButtonVisibility(
                    defaultName,
                    binding.editDetailsName.text.toString(),
                    Type.NAME.toString()
                )
                hideRecyclerView()
                binding.editDetailsName.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.primary_text
                    )
                )
            } else {
                hideUpdateButton()
                binding.editDetailsName.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.hint_color
                    )
                )
            }
        }
    }
    //setUpNameFocusChangeListener()

            //setUpDoorNoFocusChangeListener()
            //setUpPinCodeFocusChangeListener()
            //setUpLandMarkFocusChangeListener()
            //setUpStreetNameFocusChangeListener()
 */