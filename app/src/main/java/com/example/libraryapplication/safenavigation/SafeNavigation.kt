package com.example.libraryapplication.safenavigation

import androidx.navigation.NavController
import androidx.navigation.NavDirections


fun NavController.safeNavigate(direction: NavDirections) {
        currentDestination?.getAction(direction.actionId)?.run { navigate(direction) }
    }