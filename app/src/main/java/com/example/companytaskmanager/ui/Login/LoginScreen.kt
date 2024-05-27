package com.example.companytaskmanager.ui.Login

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun LoginScreen(navController: NavHostController, authViewModel: AuthViewModel) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val isAuthenticated by authViewModel.isAuthenticated.collectAsState()
    val loginError by authViewModel.loginError.collectAsState()

    LaunchedEffect(isAuthenticated) {
        if (isAuthenticated) {
            navController.navigate("protected_home") {
                popUpTo("login") { inclusive = true }
            }
        } else {
            isLoading = false
        }
    }

    LaunchedEffect(loginError) {
        if (loginError != null) {
            isLoading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .pointerInput(Unit) { detectTapGestures { authViewModel.userInteraction() } }, // Detect user interactions
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        TextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                isLoading = true
                authViewModel.login(username, password)
            },
            enabled = !isLoading
        ) {
            Text("Login")
        }
        if (isLoading) {
            CircularProgressIndicator()
        }
        if (loginError != null) {
            Text(loginError!!, color = MaterialTheme.colorScheme.error)
        }
    }
}