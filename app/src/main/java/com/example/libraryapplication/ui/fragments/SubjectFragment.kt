package com.example.libraryapplication.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.libraryapplication.R
import com.example.libraryapplication.data.entities.Book
import com.example.libraryapplication.databinding.FragmentSubjectBinding
import com.example.libraryapplication.safenavigation.safeNavigate
import com.example.libraryapplication.ui.adapter.AllSubjectAdapter
import com.example.libraryapplication.ui.adapter.SubjectsAdapter
import com.example.libraryapplication.ui.adapter.model.AllSubjectModel
import com.example.libraryapplication.viewmodels.BookManagementViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SubjectFragment : Fragment(),AllSubjectAdapter.ClickListener,AllSubjectAdapter.SubjectRecycler {
    private lateinit var recyclerView: RecyclerView
    private lateinit var binding: FragmentSubjectBinding
    private lateinit var bookManagementViewModel: BookManagementViewModel
    private lateinit var adapter: AllSubjectAdapter
    private lateinit var args: SubjectFragmentArgs
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeViewModels()
        args = SubjectFragmentArgs.fromBundle(requireArguments())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_subject, container, false)
        setSubjectRecyclerView()
        return binding.root
    }

    private fun initializeViewModels() {
        bookManagementViewModel =
            ViewModelProvider(requireActivity())[BookManagementViewModel::class.java]
    }

    private fun setSubjectRecyclerView() {
        adapter = AllSubjectAdapter()
        recyclerView = binding.subjectRecyclerView
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter.setClickListener(this)
        adapter.setSubjectRecycler(this)
        setSubjectRecyclerAdapter()

    }

    private fun setSubjectRecyclerAdapter() {
        val allSubjectModels = MutableLiveData<List<AllSubjectModel>>()
        setAllSubjectModels(allSubjectModels)
        setAllSubjectsAdapter(allSubjectModels)

    }

    private fun setAllSubjectsAdapter(allSubjectModels: MutableLiveData<List<AllSubjectModel>>) {
        allSubjectModels.observe(viewLifecycleOwner) { allSubjectList ->
            adapter.setData(
                allSubjectList
            )
        }
    }

    private fun setAllSubjectModels(
        allSubjectModels: MutableLiveData<List<AllSubjectModel>>
    ) {
        bookManagementViewModel.getCategoryDetails(args.section)
            .observe(viewLifecycleOwner) { categoryList ->
                lifecycleScope.launch {
                    val allSubjectModel: ArrayList<AllSubjectModel> = arrayListOf()
                    for (category in categoryList) {
                        val bookList = bookManagementViewModel.getSubjectsBooks(category.subjectName)
                        allSubjectModel.add(
                            AllSubjectModel(
                                category.subjectName,
                                bookManagementViewModel.getUniqueIsbnBooks(bookList)
                            )
                        )
                        allSubjectModels.value = allSubjectModel
                    }

                }
            }
    }

    override fun onClickArrow(allSubjectModel: AllSubjectModel) {
        findNavController().safeNavigate(
            SubjectFragmentDirections.actionSubjectFragmentToAllBooks(allSubjectModel.subjectName))
    }
    private fun setSubjectRecycler1(recyclerView: RecyclerView, subjectBooks: List<Book>) {
        val subjectAdapter = SubjectsAdapter()
        recyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = subjectAdapter
        subjectAdapter.setData(subjectBooks)
    }

    override fun setSubjectRecycler(recyclerView: RecyclerView, subjectBooks: List<Book>) {
        setSubjectRecycler1(recyclerView,subjectBooks)
    }

}
