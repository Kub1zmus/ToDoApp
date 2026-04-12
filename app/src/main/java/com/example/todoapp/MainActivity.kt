package com.example.todoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.todoapp.ui.theme.ToDoAppTheme

data class Task(
    var task: String,
    var isDone: Boolean = false
)

var taskList = mutableStateListOf<Task>()

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ToDoAppTheme {
                ToDoScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToDoScreen(
    modifier: Modifier = Modifier
    )
{
    var textInput by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            // Tu definuj lištu
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.my_tasks),
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            TaskCreate()

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(taskList) { task ->
                    TaskRow(
                        task,
                        onCheckChanged = { newValue ->
                            // Tu povieme zoznamu: "Nájdi túto úlohu a prepíš jej stav"
                            val index = taskList.indexOf(task)

                            // Ak program nenájde úlohu s daným indexom, tak bude index -1 -> keď program nájde úlohu, pracuje s jej indexom
                            if (index != -1) {
                                taskList[index] = task.copy(isDone = newValue)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TaskRow(
    task: Task,
    onCheckChanged: (Boolean) -> Unit)
{
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(
            checked = task.isDone,
            onCheckedChange = { onCheckChanged(it) },
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = task.task,
                fontWeight = FontWeight.Bold,
                fontSize = 25.sp,
                style =
                    if (task.isDone) {
                        TextStyle(textDecoration = TextDecoration.LineThrough)
                    } else {
                        TextStyle.Default
                    }
            )
            Text(
                text = stringResource(R.string.deadline),
                color = Color.Red
            )
        }
    }
    HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray)
}

@Composable
fun TaskCreate(
    modifier: Modifier = Modifier
)
{
    var textInput by remember { mutableStateOf("") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TextField(
            value = textInput,
            label = { Text(stringResource(R.string.task_name_input)) },
            onValueChange = { textInput = it },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done,
            ),
            singleLine = true,
            modifier = Modifier
                .padding(16.dp)
                .weight(1f),
            keyboardActions = KeyboardActions(
                onDone = {
                    if (textInput.isNotBlank())
                    {
                        taskList.add(Task(task = textInput))
                        textInput = ""
                    }
                }
            )
        )
        FloatingActionButton(
            onClick = {
                if (textInput.isNotBlank())
                {
                    taskList.add(Task(task = textInput))
                    textInput = ""
                }
            },
        ) {
            Icon(
                painter = painterResource(R.drawable.check_24dp_2b2b2b_fill0_wght400_grad0_opsz24),
                contentDescription = "Done"
            )
        }
    }
    HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray)
}
