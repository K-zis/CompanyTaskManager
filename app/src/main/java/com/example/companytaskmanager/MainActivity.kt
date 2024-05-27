package com.example.companytaskmanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.companytaskmanager.navigation.AppNavigation
import com.example.companytaskmanager.ui.Login.AuthViewModel
import com.example.companytaskmanager.ui.screens.home.HomeViewModel
import com.example.companytaskmanager.ui.theme.LoginTestingTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LoginTestingTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val authViewModel: AuthViewModel = viewModel()
                    val homeViewModel: HomeViewModel = viewModel()
                    val authenticatedAsState = authViewModel.isAuthenticated.collectAsState().value

                    LaunchedEffect(authenticatedAsState) {
                        if (authenticatedAsState) {
                            navController.navigate("protected_home") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    }

                    AppNavigation(navController, authViewModel, homeViewModel)
                }
            }
        }
    }
}