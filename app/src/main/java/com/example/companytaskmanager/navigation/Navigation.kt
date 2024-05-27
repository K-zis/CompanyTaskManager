package com.example.companytaskmanager.navigation

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.companytaskmanager.ui.Login.AuthViewModel
import com.example.companytaskmanager.ui.Login.LoginScreen
import com.example.companytaskmanager.ui.screens.home.HomeScreen
import com.example.companytaskmanager.ui.screens.home.HomeViewModel

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun AppNavigation(navController: NavHostController, authViewModel: AuthViewModel, homeViewModel: HomeViewModel) {
    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(navController, authViewModel)
        }
        composable("protected_home") {
            HomeScreen(Modifier, navController, authViewModel, homeViewModel)

        }
    }
}