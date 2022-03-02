package com.example.libraryapplication.viewmodels

import android.util.Log
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.libraryapplication.data.entities.Address
import com.example.libraryapplication.data.entities.AuthenticationDetail
import com.example.libraryapplication.data.entities.User
import com.example.libraryapplication.data.repositary.LibraryRepository
import com.example.libraryapplication.viewmodels.response.Report
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class AuthenticationViewModel @Inject constructor(private val libraryRepository: LibraryRepository) :
    ViewModel() {
    private lateinit var _user: User
    suspend fun loginCheck(mobileNo: String, password: String): Report.LoginResponse {
        for (user in libraryRepository.getAllUser()) {
            if (user.phoneNo == mobileNo) {
                return if (libraryRepository.getPassword(user.id) == password) {
                    setUser(user)
                    Report.LoginResponse.SUCCESS
                } else Report.LoginResponse.INVALID_PASSWORD
            }
        }
        return Report.LoginResponse.INVALID_MOBILE_NO
    }

    suspend fun signUpCheck(
        userName: String,
        mobileNumber: String,
        password: String,
        confirmPassword: String,
        pinCode: String,
        doorNo: String,
        district: String,
        landMark: String,
        streetName: String
    ): Report.SignupResponse {
        return when {
            inputCheck(
                userName,
                mobileNumber,
                password,
                confirmPassword,
                pinCode,
                doorNo,
                district,
                landMark,
                streetName
            ) -> Report.SignupResponse.UN_FILLED_ROWS
            !isValidMobileNo(mobileNumber) -> Report.SignupResponse.INVALID_MOBILE_NO
            isMobileAlreadyExists(mobileNumber) -> Report.SignupResponse.MOBILE_NO_ALREADY_TAKEN
            !isValidPassword(password) -> Report.SignupResponse.INVALID_PASSWORD
            password != confirmPassword -> Report.SignupResponse.PASSWORD_CONFIRMATION_FAILED
            !isValidPinCode(pinCode) -> Report.SignupResponse.INVALID_PIN_CODE
            else -> {
                setSignUpSuccess(
                    doorNo,
                    district,
                    pinCode,
                    landMark,
                    streetName,
                    userName,
                    mobileNumber,
                    password
                )
                Report.SignupResponse.SUCCESS
            }
        }
    }

    private suspend fun setSignUpSuccess(
        doorNo: String,
        district: String,
        pinCode: String,
        landMark: String,
        streetName: String,
        userName: String,
        mobileNumber: String,
        password: String
    ) {
        //  val address = "$doorNo,$district,$pinCode"
        val address = Address(doorNo, landMark, streetName, district, pinCode)
        val user = User(0, userName, mobileNumber, address)
        val id = insertUser(user).toInt()
        Log.d("string", "  viewmodel $address")
        setUser(User(id, userName, mobileNumber, address))
        insertAuthenticationDetail(AuthenticationDetail(id, password))
    }


    fun isValidPinCode(pinCode: String): Boolean {
        return pinCode.isDigitsOnly() && pinCode.length == 6 && pinCode.startsWith('6', true)
    }

    fun getUser() = _user
    private fun setUser(user: User) {
        _user = user
    }

    fun setUser(userId: Int) {
        _user = runBlocking { libraryRepository.getUser(userId) }
    }

    suspend fun isValidPassword(password: String, userId: Int): Boolean {
        return password == libraryRepository.getPassword(userId)
    }

    fun updateUserDetails(
        userName: String,
        doorNo: String,
        landMark: String,
        district: String,
        streetName: String,
        pinCode: String,
        userId: Int
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            libraryRepository.updateUserDetails(
                userName, doorNo, landMark, district, streetName, pinCode, userId
            )
        }
    }

    fun getUser(userId: Int): LiveData<User> {
        return libraryRepository.getUserDetail(userId)
    }

    suspend fun insertUser(user: User): Long {
        return libraryRepository.insertUser(user)
    }

    fun insertAuthenticationDetail(authenticationDetail: AuthenticationDetail) {
        viewModelScope.launch { libraryRepository.insertAuthenticationDetail(authenticationDetail) }
    }

    fun updateUserPassword(password: String, userId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            libraryRepository.updateUserPassword(
                password,
                userId
            )
        }
    }

    fun isValidPassword(password: String): Boolean {
        return hasSmallLetters(password) && hasCapitalLetters(password) && hasDigits(password) && hasSpecialCharacters(
            password
        )
    }

    private fun hasSmallLetters(password: String): Boolean {
        password.forEach { if (it in 'a'..'z') return true }
        return false
    }

    private fun hasCapitalLetters(password: String): Boolean {
        password.forEach { if (it in 'A'..'Z') return true }
        return false
    }

    private fun hasDigits(password: String): Boolean {
        password.forEach { if (it in '0'..'9') return true }
        return false
    }

    private fun hasSpecialCharacters(password: String): Boolean {
        password.forEach { if (it == '!' || it == '@' || it == '#' || it == '$' || it == '%' || it == '^' || it == '*') return true }
        return false
    }

    fun isValidMobileNo(mobileNumber: String) =
        mobileNumber.length == 10 && mobileNumber.isDigitsOnly()


    suspend fun isMobileAlreadyExists(mobileNumber: String): Boolean {
        for (user in libraryRepository.getAllUser()) {
            if (user.phoneNo == mobileNumber) return true
        }
        return false
    }

    private fun inputCheck(
        userName: String,
        mobileNumber: String,
        password: String,
        confirmPassword: String,
        pinCode: String,
        doorNo: String,
        district: String,
        landMark: String,
        streetName: String
    ): Boolean {
        return userName.isEmpty() || mobileNumber.isEmpty() || password.isEmpty()
                || confirmPassword.isEmpty() || pinCode.isEmpty() || doorNo.isEmpty() || district.isEmpty()
                || landMark.isEmpty() || streetName.isEmpty()
    }

    fun updateUserMobileNo(mobileNumber: String, userId: Int) {
        viewModelScope.launch { libraryRepository.updateUserMobileNo(mobileNumber, userId) }
    }


}