package com.example.companytaskmanager.ui.screens.home

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.companytaskmanager.ui.Login.AuthViewModel
import com.example.companytaskmanager.model.Todo

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    authViewModel: AuthViewModel,
    homeViewModel: HomeViewModel) {

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val isAuthenticated by authViewModel.isAuthenticated.collectAsState()
        val todos by homeViewModel.todos.collectAsState()
        val loginError by authViewModel.loginError.collectAsState()
        val todoError by homeViewModel.todoError.collectAsState()
        var searchQuery by rememberSaveable { mutableStateOf("") }

        var newTodoTitle by rememberSaveable { mutableStateOf("") }
        var newTodoContent by rememberSaveable {
            mutableStateOf("")
        }
        var newTodoCompleted by rememberSaveable {
            mutableStateOf(false)
        }

        LaunchedEffect(isAuthenticated) {
            if (!isAuthenticated) {
                navController.navigate("login") {
                    popUpTo("protected_home") { inclusive = true }
                }
            }
        }

        LaunchedEffect(todoError) {
            todoError?.let {
                // Handle the error as needed, such as showing a dialog
            }
        }

        // Fetch todos when the screen is composed
        LaunchedEffect(Unit) {
            homeViewModel.fetchTodos()
        }

        LaunchedEffect(loginError) {
            loginError?.let {
                // Handle the error as needed, such as showing a dialog
            }
        }


        Column(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) { detectTapGestures { authViewModel.userInteraction() } } // Detect user interactions
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                horizontalArrangement = Arrangement.Center
            ){
                Text("Welcome to the protected page!")
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        authViewModel.logout()
                        navController.navigate("login")
                    }
                ) {
                    Text("Logout")
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Search Todos") },
                    modifier = Modifier.weight(1f),
                    leadingIcon = {
                        IconButton(onClick = { homeViewModel.searchTodos(searchQuery) }) {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        }
                    },
                    trailingIcon = {
                        IconButton(onClick = {
                            homeViewModel.fetchTodos()
                            searchQuery = ""
                        }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear Text")
                        }
                    }
                )
            }

            // Display Todoo list
            todos?.let { todoList ->
                TodoList(modifier, todoList, homeViewModel)
            } ?: run {
                CircularProgressIndicator()
            }

            Column {
                // Other content...

                OutlinedTextField(
                    value = newTodoTitle,
                    onValueChange = { newTodoTitle = it },
                    label = { Text("Enter Todo Title") },
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                OutlinedTextField(
                    value = newTodoContent,
                    onValueChange = { newTodoContent = it },
                    label = { Text("Enter Content of Todo") },
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Text(text = "Completed?")
                Checkbox(
                    checked = newTodoCompleted,
                    onCheckedChange =
                        {newTodoCompleted = !newTodoCompleted}
                )

                Button(
                    onClick = {
                        if (newTodoTitle.isNotBlank()) {
                            homeViewModel.createTodo(newTodoTitle, newTodoContent, newTodoCompleted)
                            newTodoTitle = "" // Clear the input field after creating todo
                            newTodoContent = "" // Clear the input field after creating todo
                            newTodoCompleted = false
                        }
                    },
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Text("Create Todo")
                }
            }
        }
    }
}


@Composable
fun TodoList(
    modifier: Modifier,
    todos: List<Todo>,
    homeViewModel: HomeViewModel
) {
    var showAlertBox by rememberSaveable {
        mutableStateOf(false)
    }
    var selectedTodoId by rememberSaveable {
        mutableIntStateOf(-1)
    }
    LazyColumn {
        items(todos) { todo ->
            Row(horizontalArrangement = Arrangement.SpaceBetween) {
                Column(
                    modifier = modifier.padding(8.dp)
                ) {
                    Text(todo.title, fontWeight = FontWeight.Bold)
                    Text(todo.content)
                }
                Canvas(
                    modifier = modifier
                        .size(25.dp)
                        .clickable(onClick = {
                            todo.completed = !todo.completed
                            homeViewModel.updateTodo(todo)
                        }),
                    contentDescription = "completed_sign",
                    onDraw = {
                        drawCircle(color = if (todo.completed) Color.Green else Color.Red)
                    }
                )
                Button(onClick = {
                    selectedTodoId = todo.id
                    showAlertBox = !showAlertBox
                }) {
                    Text(text = "DELETE")
                }
            }
        }
    }
    if (showAlertBox) {
        CustomAlertDialog(
            showAlertBox = { showAlertBox = !showAlertBox },
            confirmAction = { homeViewModel.deleteTodo(selectedTodoId) })
    }

}


@Composable
fun CustomAlertDialog(
    modifier: Modifier = Modifier,
    showAlertBox: () -> Unit,
    confirmAction: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {},
        title = { Text(text = "Delete Todo")},
        text = { Text(text = "Are you sure you want to delete the Todo item?")},
        confirmButton = {
            Button(
                onClick = {
                    confirmAction()
                    showAlertBox()
                },
                colors = ButtonDefaults.buttonColors(Color.Blue)
            ) {
                Text(text = "Yes")
            }
        },
        dismissButton = {
            Button(
                onClick = showAlertBox,
                colors = ButtonDefaults.buttonColors(Color.Red)
            ) {
                Text(text = "No")
            }
        }
    )
}