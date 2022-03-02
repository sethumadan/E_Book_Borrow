package com.example.libraryapplication.ui.activity

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.libraryapplication.R
import com.example.libraryapplication.ui.utils.ConnectionLiveData
import com.example.libraryapplication.ui.utils.infoDialog
import com.example.libraryapplication.viewmodels.AuthenticationViewModel
import com.example.libraryapplication.viewmodels.BookManagementViewModel
import com.example.libraryapplication.viewmodels.UserTransactionViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var authenticationViewModel: AuthenticationViewModel
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var userTransactionViewModel: UserTransactionViewModel
    private lateinit var bookManagementViewModel: BookManagementViewModel
    private lateinit var navController: NavController
    private lateinit var connectionLiveData: ConnectionLiveData
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        connectionLiveData=ConnectionLiveData(this)
        initializeViewModel()
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host) as NavHostFragment
        navController = navHostFragment.navController
        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.setupWithNavController(navController)
        val appBarConfiguration = AppBarConfiguration(setOf(R.id.home2,R.id.searchFragment,R.id.profile))
        setupActionBarWithNavController(navController,appBarConfiguration)
        destinationChanger()
        window.navigationBarColor = resources.getColor(R.color.black)
    }
    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, findViewById(R.id.main_activity))
    }

    private fun destinationChanger() {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.bookInfo -> {
                    hideBottomNav()
                }
                R.id.subjectFragment -> hideBottomNav()
                R.id.bookList -> hideBottomNav()
                R.id.loginFragment -> {
                    hideBottomNav()
                    showActionBar()
                    supportActionBar?.setDisplayHomeAsUpEnabled(false)
                    supportActionBar?.setHomeButtonEnabled(false)
                }
                R.id.signUpFragment -> {
                    hideBottomNav()
                    showActionBar()
                }
                R.id.searchFragment -> {
                    showBottomNav()
                    supportActionBar?.setDisplayHomeAsUpEnabled(false)
                }
                R.id.profile -> {
                    showBottomNav()
                    supportActionBar?.setDisplayHomeAsUpEnabled(false)
                }
                R.id.borrowedBooksFragment -> hideBottomNav()
                R.id.returnFragment -> hideBottomNav()
                R.id.sharedBooksFragment -> hideBottomNav()
                R.id.mostlyBorrowedFragment -> hideBottomNav()
                R.id.favoriteBooksFragment -> hideBottomNav()
                R.id.shareBookFragment -> hideBottomNav()
                R.id.helpFragment -> hideBottomNav()
                R.id.welcomeFragment -> {
                    hideBottomNav()
                    hideActionBar()
                }
                R.id.editDetailsFragment -> hideBottomNav()
                R.id.category -> hideBottomNav()
                else -> {
                    showBottomNav()
                    showActionBar()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        connectionLiveData.observe(this){isNetWorkAvailable->
            if(!isNetWorkAvailable){
                infoDialog(this)
            }

        }

    }

    private fun showActionBar() {
        supportActionBar?.show()
    }

    private fun hideActionBar() {
        supportActionBar?.hide()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initializeViewModel() {
        authenticationViewModel = ViewModelProvider(this)[AuthenticationViewModel::class.java]
        userTransactionViewModel = ViewModelProvider(this)[UserTransactionViewModel::class.java]
        bookManagementViewModel = ViewModelProvider(this)[BookManagementViewModel::class.java]
    }

    private fun showBottomNav() {
        bottomNavigationView.visibility = View.VISIBLE
    }

    private fun hideBottomNav() {
        bottomNavigationView.visibility = View.GONE
    }

    fun setActionBarTitle(title: String) {
        supportActionBar?.title = title
    }
}
