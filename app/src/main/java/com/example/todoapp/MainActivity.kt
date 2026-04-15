package com.example.todoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.todoapp.ui.theme.ToDoAppTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


// Vytvorí data class Task, do ktorej sa budú zapisovať všetky úlohy
data class Task(
    var task: String,
    // TODO -> Pridaj, aby sa na úlohe mohol pridať aj custom deadline
    var deadline: LocalDateTime = LocalDateTime.now(),
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
fun ToDoScreen()
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
                colors = TopAppBarDefaults.topAppBarColors(
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
                                taskList[index] =
                                    task.copy(isDone = isDoneChange)     // Program nahradí danú úlohu jeho kópiou,
                                // no s novou hodnotou vlastnosti isDone
                                if (isDoneChange) {
                                    scope.launch {


                                        delay(1000L)
                                        if (taskList.contains(taskList.getOrNull(index)) && taskList[index].isDone) {    // POISTKA: Pozrieme sa, či je tá úloha stále v zozname
                                            // a či je STÁLE zaškrtnutá (či si ju medzitým neodškrtol)
                                            taskList.removeAt(index)
                                        }
                                    }
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
    val format = DateTimeFormatter.ofPattern("dd.MM.   HH:mm")
    val date = task.deadline.format(format)

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
                text = "$date",
                color = Color.Red
            )
        }
    }
    // Čiara, ktorá oddelí dané úlohy od seba
    HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray)
}

// Funkcia definuje UI pre zadávanie novej úlohy
@ExperimentalMaterial3Api
@Composable
fun TaskCreate()
{
    var textInput by remember { mutableStateOf("") }
    val keyboardControl = LocalSoftwareKeyboardController.current

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    val selectedDateMillis = datePickerState.selectedDateMillis

    var selectedYear by remember { mutableStateOf<Int?>(null) }
    var selectedMonth by remember { mutableStateOf<Int?>(null) }
    var selectedDay by remember { mutableStateOf<Int?>(null) }

    var showTimePicker by remember { mutableStateOf(false) }
    val timePickerState = rememberTimePickerState(
        initialHour = 12,
        initialMinute = 0,
        is24Hour = true
    )

    var selectedHour by remember { mutableIntStateOf(12) }
    var selectedMinute by remember { mutableIntStateOf(0)}


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
            verticalArrangement = Arrangement.Center) {
        Row(verticalAlignment = Alignment.CenterVertically) {
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
                            day = selectedDay,
                            month = selectedMonth,
                            year = selectedYear,
                            onSuccess = {
                                textInput = ""
                                selectedDay = null
                                selectedMonth = null
                                selectedYear = null
                                selectedHour = 12
                                selectedMinute = 0
                            },
                            hour = selectedHour,
                            minute = selectedMinute,
                            keyboardControl = keyboardControl
                        )
                    }
                )
            )
            // Tlačidlo na vyvolanie DatePicker

            FloatingActionButton(
                onClick = {
                    showDatePicker = true
                },
            ) {
                Icon(
                    painter = painterResource(R.drawable.calendar_icon),
                    contentDescription = "Choose deadline date"
                )
            }

            if (showDatePicker) {
                DatePickerDialog(
                    onDismissRequest = { showDatePicker = false }, // Keď klikneš mimo
                    confirmButton = {
                        TextButton(onClick = {
                            showDatePicker = false
                            // Tu už môžeš pracovať s datePickerState.selectedDateMillis

                            if (selectedDateMillis != null) {
                                val date = Instant
                                    .ofEpochMilli(selectedDateMillis)   // Epoch Millis je spôsob, akým počítače vnímajú čas.
                                                                        // Je to počet milisekúnd, ktoré uplynuli od 1. januára 1970. Pre človeka je to len obrovské číslo
                                                                        // Instant.ofEpochMilli vezme toto šialené číslo a povie: "Dobre, teraz z tohto čísla urobím konkrétny moment v čase"

                                    .atZone(ZoneId.systemDefault())     // Nastavíme časové pásmo na lokálne -> podľa predvolení zariadenia
                                    .toLocalDate()                      // Povieme programu: "Chceme len dátum, zahoď info o čase"

                                selectedDay = date.dayOfMonth
                                selectedMonth = date.monthValue
                                selectedYear = date.year
                            }
                            showTimePicker = true
                        } )
                        {
                            Text(stringResource(R.string.done))
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDatePicker = false }) {
                            Text(stringResource(R.string.cancel))
                        }
                    }
                ) {
                    DatePicker(state = datePickerState) // Tu je ten samotný kalendár vnútri popupu
                }
            }

            if (showTimePicker)
            {
                AlertDialog(
                    onDismissRequest = { showTimePicker = false },
                    confirmButton = {
                        TextButton(onClick = {
                            showTimePicker = false

                            selectedHour = timePickerState.hour
                            selectedMinute = timePickerState.minute
                        }) {
                            Text(stringResource(R.string.done))
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = {
                            showTimePicker = false
                        }) {
                            Text(stringResource(R.string.cancel))
                        }
                    },
                    text = {
                        TimePicker(state = timePickerState)
                    }
                )
            }
        }

        val year = selectedYear
        val month = selectedMonth
        val day = selectedDay
        val hour = selectedHour
        val minute = selectedMinute

        val finalDeadline =
            if (day != null && month != null && year != null) {
                LocalDateTime.of(year, month, day, hour, minute)
            } else {
                LocalDateTime.now()
            }

        val date = finalDeadline?.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")) ?: ""

        if (date.isNotEmpty()) {
            Text(
                text = date,
                color = Color.Gray,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }

    // Čiara, ktorá oddelí UI pre zadanie úlohy od listu úloh
    HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray)
}

// Keďže sa odoslanie novej úlohy do listu dá urobiť aj cez enter na klávesnici,
// aj cez tlačidlo done, vytvoril som novú funkciu pre jednoduchosť a efektivitu kódu
fun done(
    textInput: String,
    day: Int?, month: Int?, year: Int?,
    hour: Int, minute: Int,
    onSuccess: () -> Unit,
    keyboardControl: SoftwareKeyboardController?
)
{
    val trimmedInput = textInput.trim()
    val isATask = taskList.any { it.task.equals(trimmedInput, ignoreCase = true)}

    if (textInput.isNotBlank() && !isATask)
    {
        val finalDeadline =
            if (day != null && month != null && year != null) {
                LocalDateTime.of(year, month, day, hour, minute)
            } else {
                LocalDateTime.now()
            }
        taskList.add(Task(task = textInput, deadline = finalDeadline))
        onSuccess()
        keyboardControl?.hide()
    }
}
