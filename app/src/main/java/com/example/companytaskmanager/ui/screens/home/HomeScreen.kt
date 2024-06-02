package com.example.companytaskmanager.ui.screens.home

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.navigation.NavHostController
import com.example.companytaskmanager.model.Todo
import com.example.companytaskmanager.ui.Login.AuthViewModel
import com.example.companytaskmanager.ui.Login.LoginState
import com.example.companytaskmanager.utils.TodosResourceState

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    authViewModel: AuthViewModel,
    homeViewModel: HomeViewModel
) {
    val context = LocalContext.current
    val loginState by authViewModel.loginState.collectAsState()

    LaunchedEffect(loginState) {
        if (loginState == LoginState.Idle || loginState is LoginState.Error) {
            navController.navigate("login") {
                popUpTo("protected_home") { inclusive = true }
            }
        }
    }

    when (loginState) {
        is LoginState.Error -> {
            val errorMessage = (loginState as LoginState.Error).message
            Text(text = errorMessage)
            authViewModel.logout()
        }
        LoginState.Idle -> {authViewModel.logout()}
        LoginState.Loading -> CircularProgressIndicator()
        LoginState.Success -> HomeScreenOnSuccess(
            modifier,
            navController,
            authViewModel,
            homeViewModel,
            context
        )
    }
}

@Composable
fun HomeScreenOnSuccess(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    authViewModel: AuthViewModel,
    homeViewModel: HomeViewModel,
    context: Context
) {
    val todosResourceState by homeViewModel.todosResourceState.collectAsState()
    val todoState by homeViewModel.todoState.collectAsState()
    val suggestions by homeViewModel.suggestions.collectAsState()
    var searchQuery by rememberSaveable { mutableStateOf("") }

    var newTodoTitle by rememberSaveable { mutableStateOf("") }
    var newTodoContent by rememberSaveable {
        mutableStateOf("")
    }
    var newTodoCompleted by rememberSaveable {
        mutableStateOf(false)
    }

    var toastText by rememberSaveable {
        mutableStateOf("")
    }

    // Fetch todos when the screen is composed
    LaunchedEffect (todosResourceState) {
        if (todosResourceState is TodosResourceState.Loading) {
            homeViewModel.fetchTodos()
        }
    }


    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) { detectTapGestures { authViewModel.userInteraction() } } // Detect user interactions
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = modifier.weight(0.15f)
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
            SearchFieldWithSuggestion(
                modifier = modifier.weight(0.15f),
                keyboardController = LocalSoftwareKeyboardController.current,
                searchQuery = searchQuery,
                onSearchQueryChange = {
                    searchQuery = it
                    homeViewModel.fetchSuggestions(it)
                },
                suggestions = suggestions,
                onSuggestionClick = { todo ->
                    searchQuery = todo.title
                    homeViewModel.searchTodos(todo.title)
                },
                onSearchButtonClick = {
                    homeViewModel.searchTodos(searchQuery)
                },
                onDelete = {
                    searchQuery = ""
                    homeViewModel.fetchTodos()
                }
            )
            Spacer(modifier = Modifier.height(4.dp))
            // Display Todoo list
            when(todosResourceState) {
                is TodosResourceState.Error -> {
                    val errorMessage = (todosResourceState as TodosResourceState.Error).error
                    Text(text = errorMessage)
                }
                is TodosResourceState.Loading -> CircularProgressIndicator()
                is TodosResourceState.Success -> {
                    val todoList = (todosResourceState as TodosResourceState.Success).data
                    TodoList(
                        modifier,
                        todoList,
                        homeViewModel,
                        onUpdateTodo={toastText = "Todo updated!"},
                        onDeleteTodo={toastText = "Todo deleted!"})
                }
            }

            TodoInsertionForm(
                modifier = modifier.weight(1f),
                title = newTodoTitle,
                onTitleChange = { newTodoTitle = it},
                content = newTodoContent,
                onContentChange = { newTodoContent = it},
                completed = newTodoCompleted,
                onCompletedChange = { newTodoCompleted = !newTodoCompleted },
                keyboardController = LocalSoftwareKeyboardController.current,
                localFocusManager = LocalFocusManager.current,
                onAddButtonClick = {
                    if (newTodoTitle.isNotBlank()) {
                        homeViewModel.createTodo(
                            Todo(
                                null,
                                newTodoTitle,
                                newTodoContent,
                                newTodoCompleted
                                )
                            )
                        newTodoTitle = ""
                        newTodoContent = ""
                        newTodoCompleted = false
                        toastText = "Todo created!"
                    }
                }
            )

            if  (todoState.loading) {
                Popup (
                    alignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (todoState.success) {
                homeViewModel.fetchTodos()
                Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show()
                homeViewModel.resetTodoState()
            } else if (todoState.errorMessage != null) {
                Toast.makeText(context, todoState.errorMessage, Toast.LENGTH_SHORT).show()
                homeViewModel.resetTodoState()
            }
        }
    }
}



@Composable
fun TodoList(
    modifier: Modifier,
    todos: List<Todo>,
    homeViewModel: HomeViewModel,
    onUpdateTodo: () -> Unit,
    onDeleteTodo: () -> Unit,
) {
    var showAlertBox by rememberSaveable {
        mutableStateOf(false)
    }
    var selectedTodoId by rememberSaveable {
        mutableIntStateOf(-1)
    }
    LazyColumn (
        modifier = modifier.fillMaxWidth()
    ){
        items(todos) { todo ->
            Row(horizontalArrangement = Arrangement.SpaceBetween) {
                Column(
                    modifier = modifier
                        .padding(8.dp)
                        .weight(1f)
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
                            onUpdateTodo()
                        })
                        .weight(0.5f)
                        .align(Alignment.CenterVertically),
                    contentDescription = "completed_sign",
                    onDraw = {
                        drawCircle(color = if (todo.completed) Color.Green else Color.Red)
                    }
                )
                IconButton(
                    onClick = {
                        selectedTodoId = todo.id!!
                        showAlertBox = !showAlertBox
                    },
                    modifier = modifier
                        .weight(0.5f)
                        .align(Alignment.CenterVertically)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete todo",
                    )
                }
            }
        }
    }
    if (showAlertBox) {
        CustomAlertDialog(
            showAlertBox = { showAlertBox = !showAlertBox },
            confirmAction = {
                homeViewModel.deleteTodo(selectedTodoId)
                onDeleteTodo()
            })
    }

}

@Composable
fun SearchFieldWithSuggestion(
    modifier: Modifier = Modifier,
    keyboardController: SoftwareKeyboardController?,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    suggestions: List<Todo>?,
    onSuggestionClick: (Todo) -> Unit,
    onSearchButtonClick: () -> Unit,
    onDelete: () -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    Column{
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            TextField(
                value = searchQuery,
                onValueChange = {
                    onSearchQueryChange(it)
                    expanded = it.isNotEmpty()
                },
                label = { Text("Search Todos") },
                modifier = Modifier.weight(1f),
                leadingIcon = {
                    IconButton(onClick = {
                        onSearchButtonClick()
                        expanded = false
                    }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                },
                trailingIcon = {
                    IconButton(onClick = {
                        onDelete()
                        expanded = false
                    }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear Text")
                    }
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions {
                    onSearchButtonClick()
                    expanded = false
                    keyboardController?.hide()
                }

            )
        }

        if (expanded && !suggestions.isNullOrEmpty()) {
            Popup(
                alignment = Alignment.TopCenter,
                offset = IntOffset(0, 150), // Adjust the offset based on your layout
            ) {

                Card(
                    modifier = modifier
                        .fillMaxWidth()
                        .heightIn(max = 200.dp)
                        .padding(horizontal = 16.dp)
                    , // Limit the height of the suggestions list

                    shape = MaterialTheme.shapes.medium,
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    LazyColumn (
                        modifier = modifier
                    ){
                        items(suggestions) { todo ->
                            ListItem(
                                modifier = Modifier
                                    .clickable {
                                        onSuggestionClick(todo)
                                        expanded =
                                            false // Close the suggestions on selection
                                    },
                                headlineContent = { Text(todo.title, style = MaterialTheme.typography.bodyMedium) },
                                supportingContent = { Text(todo.content, style = MaterialTheme.typography.bodySmall, color = Color.Gray) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TodoInsertionForm(
    title: String,
    onTitleChange: (String) -> Unit,
    content: String,
    onContentChange: (String) -> Unit,
    completed: Boolean,
    onCompletedChange: (Boolean) -> Unit,
    onAddButtonClick: () -> Unit,
    keyboardController: SoftwareKeyboardController?,
    localFocusManager: FocusManager,
    modifier: Modifier,

) {
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        TextField(
            value = title,
            onValueChange = onTitleChange,
            label = { Text("Title") },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = {
                localFocusManager.moveFocus(FocusDirection.Down)
            }),
            modifier = modifier.fillMaxWidth()
        )
        Spacer(modifier = modifier.height(2.dp))
        TextField(
            value = content,
            onValueChange = onContentChange,
            label = { Text("Content") },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
                keyboardController?.hide()
            }),
            modifier = modifier.fillMaxWidth()
        )
        Spacer(modifier = modifier.height(2.dp))
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(text = "Completed?", modifier = modifier.weight(1f))
            Checkbox(
                checked = completed,
                onCheckedChange = onCompletedChange,
                modifier = modifier.weight(1f)
            )
        }
        Spacer(modifier = modifier.height(2.dp))
        Button(
            onClick = onAddButtonClick,
            modifier = modifier.align(Alignment.End)
        ) {
            Text("Create Todo")
        }
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