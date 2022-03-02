package com.example.libraryapplication.ui.fragments

import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.libraryapplication.R
import com.example.libraryapplication.databinding.FragmentReturnBinding
import com.example.libraryapplication.safenavigation.safeNavigate
import com.example.libraryapplication.ui.fragments.args.FromIcon
import com.example.libraryapplication.ui.utils.LoadImage
import com.example.libraryapplication.viewmodels.BookManagementViewModel
import com.example.libraryapplication.viewmodels.UserTransactionViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ReturnDetailsFragment : Fragment() {
    private lateinit var binding: FragmentReturnBinding
    private lateinit var args: ReturnDetailsFragmentArgs
    private lateinit var userTransactionViewModel: UserTransactionViewModel
    private lateinit var bookManagementViewModel: BookManagementViewModel
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeViewModels()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_return, container, false)
        args = ReturnDetailsFragmentArgs.fromBundle(requireArguments())
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initializeViewModels() {
        userTransactionViewModel =
            ViewModelProvider(requireActivity())[UserTransactionViewModel::class.java]
        bookManagementViewModel =
            ViewModelProvider(requireActivity())[BookManagementViewModel::class.java]
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setBook() {
        lifecycleScope.launch {
            val bookImage = bookManagementViewModel.getBook(args.borrowDetail.bookId).bookImage
            LoadImage.loadImage(bookImage, binding.returnInfoBookImage)
        }
        binding.returnInfoBookNameSelected.text = args.bookName
        binding.returnInfoBorrowDateSelected.text = args.borrowDetail.borrowDate
        binding.returnReturnDateSelected.text = args.borrowDetail.returnDate
        binding.returnExtraDaysSelected.text = args.extraDays.toString()
        binding.returnDueAmountSelected.text = args.borrowDetail.dueAmount.toString()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBook()
        onClickPay()
        onClickReturn()
    }

    private fun onClickPay() {
        if(args.borrowDetail.dueAmount>0) binding.payDueReturn.setOnClickListener { payDue() }
        else setNoDue()
    }

    private fun setNoDue() {
        binding.payDueReturn.text=resources.getString(R.string.no_due)
        binding.payDueReturn.setTextColor( ContextCompat.getColor(
            requireContext(),
            R.color.black
        ))
        binding.payDueReturn.setBackgroundColor( ContextCompat.getColor(
            requireContext(),
            R.color.no_of_borrows_color
        ))
    }

    private fun payDue(){
        findNavController().safeNavigate(
            ReturnDetailsFragmentDirections.actionReturnFragmentToPayDueFragment(
                args.borrowDetail,
                args.bookName,
                args.extraDays,
                FromIcon.RETURN.reason
            )
        )
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun onClickReturn() {
        if (args.borrowDetail.dueAmount > 0)
            binding.returnInfoReturnButton.setOnClickListener {
              payDue()
            }
        else binding.returnInfoReturnButton.setOnClickListener { confirmDialog() }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun confirmDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Confirm Return?")
        builder.setMessage("Confirm your return by clicking the \"confirm\" else \"cancel\" if not interested!!!")
        builder.setPositiveButton("return") { _, _ -> setReturnSuccess()
        }
        builder.setNegativeButton("cancel") { _, _ -> }
        builder.create().show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setReturnSuccess() {
        Toast.makeText(context, "Book has been returned Successfully", Toast.LENGTH_LONG).show()
        userTransactionViewModel.updateReturnDetails(args.borrowDetail)
        findNavController().safeNavigate(ReturnDetailsFragmentDirections.actionReturnFragmentToHome22())
    }
}