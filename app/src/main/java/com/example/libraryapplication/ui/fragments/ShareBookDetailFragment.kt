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
import com.example.libraryapplication.databinding.FragmentShareBookDetailBinding
import com.example.libraryapplication.safenavigation.safeNavigate
import com.example.libraryapplication.viewmodels.BookManagementViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ShareBookDetailFragment : DialogFragment() {
    private lateinit var args: ShareBookDetailFragmentArgs
    private lateinit var bookManagementViewModel: BookManagementViewModel
    private lateinit var binding:FragmentShareBookDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeViewModels()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_share_book_detail, container, false)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        args = ShareBookDetailFragmentArgs.fromBundle(requireArguments())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBook()
        onClickShareBookDetailButton()
    }

    private fun onClickShareBookDetailButton() {
       binding.shareBookDetailButton
            .setOnClickListener {
                findNavController().safeNavigate(
                    ShareBookDetailFragmentDirections
                        .actionShareBookDetailFragmentToHome2()
                )
            }
    }

    private fun setBook() {
        lifecycleScope.launch(Main) {
            val book = bookManagementViewModel.getBook(args.bookId)
            binding.shareBookDetailBookNameGiven.text = book.name
            binding.shareBookDetailSubjectGiven.text = book.subject
            binding.shareBookDetailAuthorGiven.text = book.authorName
            binding.shareBookDetailNoOfPagesGiven.text = book.noOfPages.toString()
        }
    }

    private fun initializeViewModels() {
        bookManagementViewModel =
            ViewModelProvider(requireActivity())[BookManagementViewModel::class.java]
    }
}