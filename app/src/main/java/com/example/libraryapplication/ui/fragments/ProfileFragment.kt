package com.example.libraryapplication.ui.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.libraryapplication.R
import com.example.libraryapplication.data.entities.User
import com.example.libraryapplication.databinding.FragmentProfileBinding
import com.example.libraryapplication.safenavigation.safeNavigate
import com.example.libraryapplication.ui.fragments.args.FromIcon
import com.example.libraryapplication.ui.utils.hideInput
import com.example.libraryapplication.viewmodels.AuthenticationViewModel
import com.example.libraryapplication.viewmodels.UserTransactionViewModel


class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var userTransactionViewModel: UserTransactionViewModel
    private lateinit var authenticationViewModel: AuthenticationViewModel
    private lateinit var user: User

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeViewModels()
        user = authenticationViewModel.getUser()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_profile, container, false
        )
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.nameProfile.hideInput()
        onClickSignOut()
        onClickDueAmount()
        onClickFavorites()
        onClickEditDetails()
        onClickBorrowCount()
        onClickDueCount()
        setUserDetails()
        updateNoOfBorrowedBooks()
        updateNoOfSharedBooks()
        onClickSharedBooks()
        updateNoOfDueBooks()
        updateNoOfFavourites()
        updateDueAmount()
    }

    private fun onClickSharedBooks() {
        binding.sharedGivenProfile.setOnClickListener {
            findNavController().safeNavigate(ProfileFragmentDirections.actionProfileToSharedBooksFragment())
        }
        binding.noOfSharedBooksProfle.setOnClickListener {
            findNavController().safeNavigate(ProfileFragmentDirections.actionProfileToSharedBooksFragment())
        }
    }

    private fun onClickDueCount() {
        binding.dueBookGivenProfile.setOnClickListener {
            findNavController().safeNavigate(
                ProfileFragmentDirections.actionProfileToBorrowedBooksFragment2(
                    FromIcon.DUE_BOOKS.reason
                )
            )
        }
        binding.noOfDueBookPrfile.setOnClickListener {
            findNavController().safeNavigate(
                ProfileFragmentDirections.actionProfileToBorrowedBooksFragment2(
                    FromIcon.DUE_BOOKS.reason
                )
            )
        }
    }

    private fun onClickBorrowCount() {
        binding.borrowedGivenProfile.setOnClickListener {
            findNavController().safeNavigate(
                ProfileFragmentDirections.actionProfileToBorrowedBooksFragment2(
                    FromIcon.BORROWED_BOOKS.reason
                )
            )
        }
        binding.noOfBorrowsProfile.setOnClickListener {
            findNavController().safeNavigate(
                ProfileFragmentDirections.actionProfileToBorrowedBooksFragment2(
                    FromIcon.BORROWED_BOOKS.reason
                )
            )
        }
    }

    private fun onClickFavorites() {
        binding.favoritesCardProfile.setOnClickListener {
            findNavController().safeNavigate(ProfileFragmentDirections.actionProfileToFavoriteBooksFragment())
        }
    }

    private fun onClickDueAmount() {
        binding.dueCardProfile.setOnClickListener {
            findNavController().safeNavigate(
                ProfileFragmentDirections.actionProfileToBorrowedBooksFragment2(
                    FromIcon.DUE_BOOKS.reason
                )
            )
        }
    }

    private fun onClickEditDetails() {
        binding.editProfile.setOnClickListener {
            findNavController().safeNavigate(
                ProfileFragmentDirections.actionProfileToEditDetailsFragment()
            )
        }
    }

    private fun setUserDetails() {
        authenticationViewModel.getUser(user.id).observe(viewLifecycleOwner) {
            binding.nameProfile.text = it.name
            binding.mobileProfile.text = it.phoneNo
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateDueAmount() {
        binding.dueAmountaGivenProfile.text =
            userTransactionViewModel.getUserBalanceDueAmount().toString()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateNoOfFavourites() {
        userTransactionViewModel.getFavoriteBooks(user.id).observe(viewLifecycleOwner) {
            binding.favouritesGivenProfile.text = it.size.toString()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateNoOfDueBooks() {
        userTransactionViewModel.getUserDueBookDetails(user).observe(viewLifecycleOwner) {
            binding.dueBookGivenProfile.text = it.size.toString()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateNoOfSharedBooks() {
        userTransactionViewModel.getUserDonatedBooksLive(user).observe(viewLifecycleOwner) {
            binding.sharedGivenProfile.text = it.size.toString()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateNoOfBorrowedBooks() {
        userTransactionViewModel.getBorrowBookCount(user).observe(viewLifecycleOwner) {
            binding.borrowedGivenProfile.text = it.toString()
        }
    }

    private fun onClickSignOut() {
        binding.logout.setOnClickListener {
            confirmDialog()
        }
    }

    private fun confirmDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Logout?")
        builder.setMessage("Are you sure? You Want to Logout from the Library")
        builder.setPositiveButton("Logout") { _, _ -> signOut() }
        builder.setNegativeButton("Cancel") { _, _ -> }
        builder.create().show()
    }

    private fun signOut() {
        val sharedPrefLogin =
            requireActivity().getSharedPreferences("Login", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPrefLogin?.edit()!!
        editor.putBoolean("loggedIn", false)
        editor.putInt("userId", 0)
        editor.apply()
        findNavController().safeNavigate(ProfileFragmentDirections.actionProfileToLoginFragment())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initializeViewModels() {
        userTransactionViewModel =
            ViewModelProvider(requireActivity())[UserTransactionViewModel::class.java]
        authenticationViewModel =
            ViewModelProvider(requireActivity())[AuthenticationViewModel::class.java]
    }
}
