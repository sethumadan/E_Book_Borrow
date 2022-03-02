package com.example.libraryapplication.ui.fragments

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.libraryapplication.R
import com.example.libraryapplication.data.entities.User
import com.example.libraryapplication.databinding.FragmentHomeBinding
import com.example.libraryapplication.enums.Subject
import com.example.libraryapplication.safenavigation.safeNavigate
import com.example.libraryapplication.ui.fragments.args.FromIcon
import com.example.libraryapplication.ui.utils.hideInput
import com.example.libraryapplication.viewmodels.AuthenticationViewModel
import com.example.libraryapplication.viewmodels.UserTransactionViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var authenticationViewModel: AuthenticationViewModel
    private lateinit var userTransactionViewModel: UserTransactionViewModel
    private lateinit var user: User
    private val borrowedBook = FromIcon.BORROWED_BOOKS.reason
    private val dueBook = FromIcon.DUE_BOOKS.reason
    private val returnBook=FromIcon.RETURN.reason

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeViewModels()
        setWelcomeScreen()
        checkAlreadyLoggedIn()
        setHasOptionsMenu(true)
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.over_flow_menu,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(item, requireView().findNavController())||
                super.onOptionsItemSelected(item)
    }
    private fun setWelcomeScreen() {
        val sharedPrefWelcome =
            requireActivity().getSharedPreferences("Welcome", Context.MODE_PRIVATE)
        val firstStart = sharedPrefWelcome.getBoolean("firstStart", true)
        if (firstStart) {
            findNavController().safeNavigate(HomeFragmentDirections.actionHome2ToWelcomeFragment())
        }
    }

    private fun checkAlreadyLoggedIn() {
        val sharedPrefLogin =
            requireActivity().getSharedPreferences("Login", Context.MODE_PRIVATE)
        val loggedIn = sharedPrefLogin?.getBoolean("loggedIn", false)
        val userId = sharedPrefLogin?.getInt("userId", 0)
        if (loggedIn == true) {
           authenticationViewModel.setUser(userId!!)
        } else {
            findNavController().safeNavigate(HomeFragmentDirections.actionHome2ToLoginFragment())
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        user = authenticationViewModel.getUser()
       updateDueAmount()
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
       setDueAmount()
        binding.borrowImage.hideInput()
        onClickFavorites()
        onClickBorrow()
        onClickReturn()
        onClickSearch()
        onClickPayDue()
        onClickSharedBooks()
        onClickBorrowedBooks()
        onClickDueBooks()
        onClickPopularImage()
        onClickShareBook()
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun  updateDueAmount(){
        userTransactionViewModel.getUserBorrowDetail(user).observe(viewLifecycleOwner) {
            userTransactionViewModel.updateDueAmount(it)
            setDueAmountVisibility(userTransactionViewModel.getUserBalanceDueAmount()>0)
            checkDue()
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun setDueAmount() {
        userTransactionViewModel.getUserDueBookDetails(user).observe(viewLifecycleOwner) {
            val dueAmount = userTransactionViewModel.getUserBalanceDueAmount()
            binding.dueGiven.text = dueAmount.toString()
        }
    }

    private fun setDueAmountVisibility(hasDue:Boolean){
        if(hasDue){
            binding.noDue.visibility=View.GONE
            binding.hasBalanceAmomtCard.visibility=View.VISIBLE
        }
       else{
            binding.hasBalanceAmomtCard.visibility=View.GONE
            binding.noDue.visibility=View.VISIBLE
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkDue() {
        if(userTransactionViewModel.getUserBalanceDueAmount()<=0){
            binding.balanceCard.setCardBackgroundColor( ContextCompat.getColor(
                requireContext(),
                R.color.card_back_ground
            ))
        }
        else {
            binding.balanceCard.setCardBackgroundColor( ContextCompat.getColor(
                requireContext(),
                R.color.primary
            ))

        }
    }

    private fun onClickShareBook() {
        binding.shareImage.setOnClickListener {
            findNavController().safeNavigate(
                HomeFragmentDirections.actionHome2ToShareBookFragment()
            )
        }
        binding.share.setOnClickListener { findNavController().safeNavigate(HomeFragmentDirections.actionHome2ToShareBookFragment()) }
    }

    private fun onClickPopularImage() {
        binding.popularImage.setOnClickListener {
            findNavController().safeNavigate(HomeFragmentDirections.actionHome2ToMostlyBorrowedFragment())
        }
        binding.popularBookText.setOnClickListener {
            findNavController().safeNavigate(HomeFragmentDirections.actionHome2ToMostlyBorrowedFragment())
        }
    }

    private fun onClickDueBooks() {
        binding.dueBookImage.setOnClickListener {
            findNavController().safeNavigate(
                HomeFragmentDirections.actionHome2ToBorrowedBooksFragment(
                     dueBook
                )
            )
        }
        binding.dueBooksImageText.setOnClickListener {
            findNavController().safeNavigate(
                HomeFragmentDirections.actionHome2ToBorrowedBooksFragment(
                    dueBook
                )
            )
        }
    }

    private fun onClickBorrowedBooks() {
        binding.borrowedBookImage.setOnClickListener {
            findNavController().safeNavigate(
                HomeFragmentDirections.actionHome2ToBorrowedBooksFragment(
                    borrowedBook
                )
            )
        }
        binding.borrowedBookImageText.setOnClickListener {
            findNavController().safeNavigate(
                HomeFragmentDirections.actionHome2ToBorrowedBooksFragment(
                    borrowedBook
                )
            )
        }
    }

    private fun onClickSharedBooks() {
        binding.sharedBooksImage.setOnClickListener {
            findNavController().safeNavigate(
                HomeFragmentDirections.actionHome2ToSharedBooksFragment()
            )
        }
        binding.sharedBooksImageText.setOnClickListener {
            findNavController().safeNavigate(
                HomeFragmentDirections.actionHome2ToSharedBooksFragment()
            )
        }
    }

    private fun onClickPayDue() {
        binding.payDueClick.setOnClickListener {
            findNavController().safeNavigate(
                HomeFragmentDirections.actionHome2ToBorrowedBooksFragment(
                     dueBook
                )
            )
        }
    }

    private fun onClickSearch() {
        binding.allBooksImage.setOnClickListener {
            findNavController().safeNavigate(HomeFragmentDirections.actionHome2ToAllBooks(Subject.ALL.reason))
        }
        binding.allBooksImageText.setOnClickListener {
            findNavController().safeNavigate(HomeFragmentDirections.actionHome2ToAllBooks(Subject.ALL.reason))
        }
    }

    private fun onClickReturn() {
        binding.returnImage.setOnClickListener {
            findNavController().safeNavigate(
                HomeFragmentDirections.actionHome2ToBorrowedBooksFragment(
                     returnBook
                )
            )
        }
        binding.returnText.setOnClickListener {
            findNavController().safeNavigate(
                HomeFragmentDirections.actionHome2ToBorrowedBooksFragment(
                    returnBook
                )
            )
        }
    }

    private fun onClickBorrow() {
        binding.borrow.setOnClickListener {
            findNavController().safeNavigate(
                HomeFragmentDirections.actionHome2ToCategory()
            )
        }
        binding.borrowImage.setOnClickListener {
            findNavController().safeNavigate(
                HomeFragmentDirections.actionHome2ToCategory(
                )
            )
        }
    }

    private fun onClickFavorites() {
        binding.favouritesImage.setOnClickListener {
            findNavController().safeNavigate(HomeFragmentDirections.actionHome2ToFavoriteBooksFragment())
        }
        binding.favoriteText.setOnClickListener {
            findNavController().safeNavigate(HomeFragmentDirections.actionHome2ToFavoriteBooksFragment())
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initializeViewModels() {
        authenticationViewModel =
            ViewModelProvider(requireActivity())[AuthenticationViewModel::class.java]
        userTransactionViewModel =
            ViewModelProvider(requireActivity())[UserTransactionViewModel::class.java]
    }
}
