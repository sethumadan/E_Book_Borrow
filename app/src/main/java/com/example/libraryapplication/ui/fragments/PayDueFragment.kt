package com.example.libraryapplication.ui.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.libraryapplication.R
import com.example.libraryapplication.databinding.FragmentPayDueBinding
import com.example.libraryapplication.viewmodels.UserTransactionViewModel


class PayDueFragment : DialogFragment() {
    private lateinit var userTransactionViewModel: UserTransactionViewModel
    private lateinit var args: PayDueFragmentArgs
    lateinit var binding:FragmentPayDueBinding

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeViewModel()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_pay_due, container, false)
        args = PayDueFragmentArgs.fromBundle(requireArguments())
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        setPaymentDetails()
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
       onClickPay()
        onClickCancel()
    }

    private fun onClickCancel() {
        binding.payDueCancel.setOnClickListener { dialog?.dismiss() }
    }

    private fun onClickPay() {
        binding.payDuePay.setOnClickListener {
            userTransactionViewModel.updateReturnDetails(args.borrowDetail)
            Toast.makeText(context, "Book has been returned Successfully", Toast.LENGTH_LONG).show()
            findNavController().navigate(PayDueFragmentDirections.actionPayDueFragmentToHome2())
            /*if (args.navigatedFrom != FromFragment.RETURN_DETAILS_FRAGMENT.reason){
               findNavController().navigate(PayDueFragmentDirections.actionPayDueFragmentToHome2())
            }
            else findNavController().navigate(PayDueFragmentDirections.actionPayDueFragmentToHome2())*/
        }
    }

    private fun setPaymentDetails() {
        val borrowDetail = args.borrowDetail
        val extraDays = args.extraDays
        val bookName = args.bookName
        binding.payDueBorrowDetailsBookNameGiven.text = bookName
        binding.payDueReturnDateBorrowdetailsGiven.text = borrowDetail.returnDate
        binding.payDueExtraDaysGiven.text = extraDays.toString()
        binding.payDueDueAmountGiven.text = borrowDetail.dueAmount.toString()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initializeViewModel() {
        userTransactionViewModel =
            ViewModelProvider(requireActivity())[UserTransactionViewModel::class.java]
    }
}