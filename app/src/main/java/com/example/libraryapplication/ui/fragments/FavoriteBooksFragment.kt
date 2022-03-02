package com.example.libraryapplication.ui.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
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
import com.example.libraryapplication.data.entities.FavoriteBook
import com.example.libraryapplication.data.entities.User
import com.example.libraryapplication.databinding.FragmentFavoriteBooksBinding
import com.example.libraryapplication.safenavigation.safeNavigate
import com.example.libraryapplication.ui.adapter.FavoriteBooksAdapter
import com.example.libraryapplication.viewmodels.AuthenticationViewModel
import com.example.libraryapplication.viewmodels.BookManagementViewModel
import com.example.libraryapplication.viewmodels.UserTransactionViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FavoriteBooksFragment : Fragment(),FavoriteBooksAdapter.ClickListener {
    private lateinit var binding: FragmentFavoriteBooksBinding
    private lateinit var userTransactionViewModel: UserTransactionViewModel
    private lateinit var authenticationViewModel: AuthenticationViewModel
    private lateinit var bookManagementViewModel: BookManagementViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FavoriteBooksAdapter
    private lateinit var user: User

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeViewModels()
        user = authenticationViewModel.getUser()
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_favorite_books, container, false)
        setFavoriteBooksRecyclerView()
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setFavoriteBooksRecyclerView() {
        adapter = FavoriteBooksAdapter(requireContext())
        recyclerView = binding.favoriteBookRecycler
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter.setClickListener(this)
        val favoriteBookList = MutableLiveData<List<Book>>()
       setFavoriteBookList(favoriteBookList)
        setFavoriteAdapterData(favoriteBookList)
    }

    private fun setFavoriteAdapterData(favoriteBookList: MutableLiveData<List<Book>>) {
        favoriteBookList.observe(viewLifecycleOwner) {
            setEmptyImageVisibility(it)
            adapter.setData(it)
        }
    }

    private fun setEmptyImageVisibility(it: List<Book>) {
        if (it.isEmpty()){
            binding.noItem.visibility = View.VISIBLE
            binding.noItemText.visibility=View.VISIBLE
        }
        else{
            binding.noItem.visibility = View.GONE
            binding.noItemText.visibility=View.GONE
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setFavoriteBookList(favoriteBookList: MutableLiveData<List<Book>>) {
        userTransactionViewModel.getFavoriteBooks(user.id)
            .observe(viewLifecycleOwner) { favoriteBooks ->
                lifecycleScope.launch(Dispatchers.Main) {
                    val books = arrayListOf<Book>()
                    for (favoriteBook in favoriteBooks) {
                        bookManagementViewModel.getAvailableCopyOfBook(favoriteBook.isbnId)
                            ?.let { books.add(it) }
                    }
                    favoriteBookList.value = bookManagementViewModel.getUniqueIsbnBooks(books)
                }
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initializeViewModels() {
        userTransactionViewModel =
            ViewModelProvider(requireActivity())[UserTransactionViewModel::class.java]
        authenticationViewModel =
            ViewModelProvider(requireActivity())[AuthenticationViewModel::class.java]
        bookManagementViewModel =
            ViewModelProvider(requireActivity())[BookManagementViewModel::class.java]
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun confirmDialog(isbnId: Int, userId: Int) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Remove ?")
        builder.setMessage("Are you sure you want to Remove this book from Favorites ?")
        builder.setPositiveButton(
            "Remove"
        ) { _, _ ->
            Toast.makeText(
                context,
                "Removed from favorites",
                Toast.LENGTH_LONG
            ).show()
            userTransactionViewModel.deleteFavorite(FavoriteBook(isbnId, userId))
        }
        builder.setNegativeButton(
            "Cancel"
        ) { _, _ -> }
        builder.create().show()
    }
    override fun onLongClickView(favoriteBook: Book) {
        confirmDialog(favoriteBook.isbnId, user.id)

    }

    override fun onClickView(favoriteBook: Book) {
        findNavController().safeNavigate(
            FavoriteBooksFragmentDirections.actionFavoriteBooksFragmentToBookInfo(
                favoriteBook
            )
        )
    }

}