package com.example.libraryapplication.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.example.libraryapplication.R
import com.example.libraryapplication.databinding.FragmentBorrowDetailsBinding
import com.example.libraryapplication.safenavigation.safeNavigate
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BorrowDetailFragment : DialogFragment() {
   private lateinit var args: BorrowDetailFragmentArgs
   private lateinit var binding: FragmentBorrowDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        args = BorrowDetailFragmentArgs.fromBundle(requireArguments())
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_borrow_details, container, false)
        setBorrowDetails()
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        binding.shareBookDetailButton.setOnClickListener {
            findNavController().safeNavigate(BorrowDetailFragmentDirections.actionBorrowDetailFragmentToHome2())
        }
        return binding.root
    }

    private fun setBorrowDetails() {
        val borrowDetail = args.borrowDetail
        val bookName = args.bookName
        binding.borrowDateBorrowdetailsGiven.text = borrowDetail.borrowDate
        binding.returnDateBorrowdetailsGiven.text = borrowDetail.returnDate
        binding.borrowDetailsBookNameGiven.text = bookName
    }
}