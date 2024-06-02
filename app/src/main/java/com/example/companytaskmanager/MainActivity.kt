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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.companytaskmanager.navigation.AppNavigation
import com.example.companytaskmanager.ui.Login.AuthViewModel
import com.example.companytaskmanager.ui.screens.home.HomeViewModel
import com.example.companytaskmanager.ui.theme.LoginTestingTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
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
                    val authViewModel: AuthViewModel = hiltViewModel()
                    val homeViewModel: HomeViewModel = hiltViewModel()
//                    val loginState = authViewModel.loginState.collectAsState().value
//
//                    LaunchedEffect(loginState) {
//                        if (authenticatedAsState) {
//                            navController.navigate("protected_home") {
//                                popUpTo("login") { inclusive = true }
//                            }
//                        }
//                    }

                    AppNavigation(navController, authViewModel, homeViewModel)
                }
            }
        }
    }
}