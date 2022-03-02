package com.example.libraryapplication.ui.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.libraryapplication.R
import com.example.libraryapplication.data.entities.*
import com.example.libraryapplication.databinding.FragmentWelcomeBinding
import com.example.libraryapplication.enums.Section
import com.example.libraryapplication.enums.Subject
import com.example.libraryapplication.safenavigation.safeNavigate
import com.example.libraryapplication.viewmodels.AuthenticationViewModel
import com.example.libraryapplication.viewmodels.BookManagementViewModel
import com.example.libraryapplication.viewmodels.UserTransactionViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStream

class WelcomeFragment : Fragment() {
    private lateinit var binding: FragmentWelcomeBinding
    private lateinit var authenticationViewModel: AuthenticationViewModel
    private lateinit var userTransactionViewModel: UserTransactionViewModel
    private lateinit var bookManagementViewModel: BookManagementViewModel
    private lateinit var json: String

    @RequiresApi(Build.VERSION_CODES.O)
    private fun prePopulateUsers() {
        lifecycleScope.launch(Dispatchers.IO) {
            authenticationViewModel.insertUser(
                User(
                    1,
                    "SethuMadan S",
                    "9629906267",
                    Address("A8","BAS Quarters","kolundampattu","Tiruvannamalai","606706")
                )
            )
            authenticationViewModel.insertAuthenticationDetail(
                AuthenticationDetail(
                    1,
                    "Qwerty@123"
                )
            )
        }
        userTransactionViewModel.updateDefaultUserDetails(
            Book(
                4,
                "What is Mathematics?",
                "Richard Courant",
                "Maths",
                592,
                "Available",
                "https://m.media-amazon.com/images/I/51fWVkYVVHL.jpg",
                1
            ), 1
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_welcome, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onClickExplore()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun onClickExplore() {
        binding.cardWelcomeStart.setOnClickListener {
            preRequired()
            val sharedPreferences =
                requireActivity().getSharedPreferences("Welcome", Context.MODE_PRIVATE)
            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            editor.putBoolean("firstStart", false)
            editor.apply()
            findNavController().safeNavigate(WelcomeFragmentDirections.actionWelcomeFragmentToLoginFragment())
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun preRequired() {
        prePopulateBooks()
        prePopulateCategories()
        prePopulateUsers()
    }

    private fun prePopulateCategories() {
        val categories: ArrayList<Category> = arrayListOf()
        categories.add(
            Category(
                Subject.MATHS.reason,
                Section.ACADEMICS.reason,
                "https://i.pinimg.com/originals/c1/d2/71/c1d271455faf7bec04fc9e2f985d3314.png"
            )
        )
        categories.add(
            Category(
                Subject.PHYSICS.reason,
                Section.ACADEMICS.reason,
                "https://media.istockphoto.com/vectors/physics-illustration-vector-id615915320?s=612x612"
            )
        )
        categories.add(
            Category(
                Subject.CHEMISTRY.reason,
                Section.ACADEMICS.reason,
                "https://media.istockphoto.com/vectors/chemistry-science-doodle-handwriting-elements-science-and-school-vector-id607772064"
            )
        )
        categories.add(
            Category(
                Subject.BIOLOGY.reason,
                Section.ACADEMICS.reason,
                "https://media.istockphoto.com/vectors/biology-background-vector-id893472974"
            )
        )
        categories.add(
            Category(
                Subject.COMPUTER.reason,
                Section.ACADEMICS.reason,
                "https://s3.amazonaws.com/tf-nightingale/2020/07/Blog--5-.jpg"
            )
        )
        categories.add(
            Category(
                Subject.TALES.reason,
                Section.NON_ACADEMICS.reason,
                "https://koustrupco.dk/media/cache/product_original/product-images/61/03/hca%20fairytales%20forside%20kvadratisk%20kortmappe%20web1563183395.3872.jpg?1563183395"
            )
        )
        categories.add(
            Category(
                Subject.HORROR.reason,
                Section.NON_ACADEMICS.reason,
                "https://images.squarespace-cdn.com/content/v1/59e57ef1914e6beddb88f4ec/1567631851145-FT47NTGGRYBIP870MA0E/Venture+Into+Horror+Square.png"
            )
        )
        categories.add(
            Category(
                Subject.HISTORY.reason,
                Section.NON_ACADEMICS.reason,
                "https://www.historyonthenet.com/wp-content/uploads/2017/01/HISTORY-unplugged-1400x1400-1024x1024.jpg"
            )
        )
        categories.add(
            Category(
                Subject.DEVOTIONAL.reason,
                Section.NON_ACADEMICS.reason,
                "https://namansmusic.com/wp-content/uploads/2020/09/Devotional.jpg"
            )
        )
        categories.add(
            Category(
                Subject.COMICS.reason,
                Section.NON_ACADEMICS.reason,
                "https://thumbs.dreamstime.com/b/color-vintage-comics-shop-emblem-colorful-vector-illustration-comic-book-style-word-isolated-white-background-83890709.jpg"
            )
        )
        bookManagementViewModel.insertCategories(categories)
    }

    private fun prePopulateBooks() {
        val books: ArrayList<Book> = arrayListOf()
        val mostBorrows = arrayListOf<MostBorrowed>()
        val jasonFile: InputStream = requireActivity().assets.open("Book.json")
        val size = jasonFile.available()
        val buffer: ByteArray = ByteArray(size)
        jasonFile.read(buffer)
        jasonFile.close()
        json = String(buffer, charset("utf-8"))
        val jasonArray: JSONArray = JSONArray(json)
        val length = jasonArray.length()
        for (i in 0 until length) {
            val jasonObject: JSONObject = jasonArray.getJSONObject(i)
            val id = jasonObject.getInt("id")
            val name = jasonObject.getString("name")
            val authorName = jasonObject.getString("authorName")
            val subject = jasonObject.getString("subject")
            val noOfPages = jasonObject.getInt("noOfPages")
            val availability = jasonObject.getString("availability")
            val bookImage = jasonObject.getString("bookImage")
            val isbnId = jasonObject.getInt("isbnId")
            books.add(
                Book(
                    id,
                    name,
                    authorName,
                    subject,
                    noOfPages,
                    availability,
                    bookImage,
                    isbnId
                )
            )
        }
        bookManagementViewModel.insertBooks(books)
        for (book in books) {
            mostBorrows.add(MostBorrowed(book.id, 0))
        }
        bookManagementViewModel.insertMostBorrows(mostBorrows)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initializeViewModel() {
        authenticationViewModel =
            ViewModelProvider(requireActivity())[AuthenticationViewModel::class.java]
        userTransactionViewModel =
            ViewModelProvider(requireActivity())[UserTransactionViewModel::class.java]
        bookManagementViewModel =
            ViewModelProvider(requireActivity())[BookManagementViewModel::class.java]

    }
}
