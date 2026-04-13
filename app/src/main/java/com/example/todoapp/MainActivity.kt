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
import androidx.compose.runtime.rememberCoroutineScope
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


// Vytvorí data class Task, do ktorej sa budú zapisovať všetky úlohy
data class Task(
    var task: String,
    // TODO -> Pridaj, aby sa na úlohe mohol pridať aj custom deadline
    var isDone: Boolean = false
)

// Vytvorí list úloh, do ktorého sa budú úlohy zapisovať a z ktorého sa budú vyberať dáta úloh pre UI
// List môže obsahovať len dátový typ Task
var taskList = mutableStateListOf<Task>()


// Hlavná funkcia, v ktorej sa tvorí Compose
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
    val scope = rememberCoroutineScope()

    // Vytvorí template pre hlavné rozhranie aplikácie
    Scaffold(
        topBar = {
            // TopBar aplikácie s textom v strede
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.my_tasks),
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                },
                // Program nastaví farby na hlavné farby pre theme apky/telefónu
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { innerPadding ->
        // Vytvorí Stĺpec pre okno na zadávanie úloh a pre list úloh, ktoré budú zobrazené
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            // Vykoná funkciu taskCreate, v ktorej je definované zobrazenie
            TaskCreate()

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(taskList) { task ->    // items() -> zoberie dáta z daného... niečoho a urobí s nimi to, čo je nižšie
                    TaskRow(
                        task = task,
                        onCheckChanged = { isDoneChange ->
                            // Program nájde úlohu, ktorej stav sa ide zmeniť
                            val index = taskList.indexOf(task)

                            // Ak program nenájde úlohu s daným indexom, tak bude index -1 -> keď program nájde úlohu, pracuje s jej indexom
                            // Ak sa checkbox zaklikne, zmení isDone vlastnosť danej úlohy, počká 1s a program vymaže danú hotovú úlohu
                            if (index != -1) {
                                scope.launch {
                                    taskList[index] = task.copy(isDone = isDoneChange) // Program nahradí danú úlohu jeho kópiou,
                                                                                       // no s novou hodnotou vlastnosti isDone
                                    delay(1000L)
                                    taskList.remove(taskList[index])
                                }

                            }
                        }
                    )
                }
            }
        }
    }
}


// Funkcia definuje, ako má vyzerať UI pre jednu úlohu
@Composable
fun TaskRow(
    task: Task,
    onCheckChanged: (Boolean) -> Unit)
{
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Checkbox pre zakliknutie, či je úloha hotová alebo nie
        Checkbox(
            checked = task.isDone,
            onCheckedChange = { onCheckChanged(it) },
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Text s názvom úlohy
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
            // Text pre deadline úlohy  TODO -> Pridaj, aby sa na úlohe mohol pridať aj custom deadline
            Text(
                text = stringResource(R.string.deadline),
                color = Color.Red
            )
        }
    }
    // Čiara, ktorá oddelí dané úlohy od seba
    HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray)
}

// Funkcia definuje UI pre zadávanie novej úlohy
@Composable
fun TaskCreate()
{
    var textInput by remember { mutableStateOf("") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Textové pole na zadanie úlohy
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
                    // Vykoná funkciu, ktorá odosiela novú úlohu do listu úloh
                    done(
                        textInput = textInput,
                        onSuccess = {textInput = ""})
                }
            )
        )
        // Tlačidlo done - odoslanie novej úlohy do listu
        FloatingActionButton(
            onClick = {
                // Vykoná funkciu, ktorá odosiela novú úlohu do listu úloh
                done(
                    textInput = textInput,
                    onSuccess = {textInput = ""}
                )
            },
        ) {
            Icon(
                painter = painterResource(R.drawable.check_24dp_2b2b2b_fill0_wght400_grad0_opsz24),
                contentDescription = "Done"
            )
        }
    }
    // Čiara, ktorá oddelí UI pre zadanie úlohy od listu úloh
    HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray)
}


// Keďže sa odoslanie novej úlohy do listu dá urobiť aj cez enter na klávesnici, aj cez tlačidlo done, vytvoril som novú funkciu pre jednoduchosť a efektivitu kódu
fun done(
    textInput: String,
    onSuccess: () -> Unit
)
{
    val trimmedInput = textInput.trim()
    val isATask = taskList.any { it.task.equals(trimmedInput, ignoreCase = true)}

    if (textInput.isNotBlank() && !isATask)
    {
        taskList.add(Task(task = textInput))
        onSuccess()
    }
}