package com.example.dogcare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dogcare.ui.theme.DogCareTheme
import com.example.dogcare.data.DogDatabase
import com.example.dogcare.repository.DogRepository
import com.example.dogcare.viewmodel.DogViewModel
import androidx.compose.ui.tooling.preview.Preview
import com.example.dogcare.data.DogTask
import com.example.dogcare.datastore.UserPreferences
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            val scope = rememberCoroutineScope()
            val userPreferences = UserPreferences(context)

            val isDarkMode by userPreferences.darkModeFlow.collectAsState(initial = false)
            val savedFilter by userPreferences.lastFilterFlow.collectAsState(initial = "ALL")

            val db = DogDatabase.getDatabase(context)
            val dao = db.dogTaskDao()
            val repository = DogRepository(dao)

            val viewModel: DogViewModel = viewModel(
                factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                        return DogViewModel(repository) as T
                    }
                }
            )

            DogCareTheme(darkTheme = isDarkMode) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(
                        viewModel = viewModel,
                        isDarkMode = isDarkMode,
                        currentFilter = savedFilter,
                        onThemeToggle = {
                            scope.launch {
                                userPreferences.saveDarkMode(!isDarkMode)
                            }
                        },
                        onFilterChange = { newFilter ->
                            scope.launch {
                                userPreferences.saveFilter(newFilter)
                            }
                        },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun MainScreen(
    viewModel: DogViewModel,
    isDarkMode: Boolean,
    currentFilter: String,
    onThemeToggle: () -> Unit,
    onFilterChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var counter by remember { mutableIntStateOf(0) }
    val allTasks by viewModel.tasks.collectAsState(initial = emptyList())

    val tasks = when (currentFilter) {
        "DONE" -> allTasks.filter { it.isDone }
        "NOT_DONE" -> allTasks.filter { !it.isDone }
        else -> allTasks
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "🎀 Routine à pitou 🐶 ",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Mode sombre")
            Switch(
                checked = isDarkMode,
                onCheckedChange = { onThemeToggle() }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = {
                    viewModel.addTask(DogTask(title = "🐕 Promenade", subtitle = "30 minutes", isDone = false))
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Ajouter promenade", maxLines = 1)
            }

            Button(
                onClick = {
                    viewModel.addTask(DogTask(title = "🧼 Bain", subtitle = "Shampoing doux"))
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Ajouter bain", maxLines = 1)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "🐾 Promenades cette semaine",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "$counter",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { counter++ }) {
                        Text("Ajouter")
                    }

                    Button(onClick = { counter = 0 }) {
                        Text("Réinitialiser")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { onFilterChange("ALL") },
                modifier = Modifier.weight(1f),
                colors = if (currentFilter == "ALL") ButtonDefaults.buttonColors() else ButtonDefaults.filledTonalButtonColors()
            ) {
                Text("Toutes", maxLines = 1)
            }

            Button(
                onClick = { onFilterChange("DONE") },
                modifier = Modifier.weight(1f),
                colors = if (currentFilter == "DONE") ButtonDefaults.buttonColors() else ButtonDefaults.filledTonalButtonColors()
            ) {
                Text("Fait", maxLines = 1)
            }

            Button(
                onClick = { onFilterChange("NOT_DONE") },
                modifier = Modifier.weight(1f),
                colors = if (currentFilter == "NOT_DONE") ButtonDefaults.buttonColors() else ButtonDefaults.filledTonalButtonColors()
            ) {
                Text("À faire", maxLines = 1)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(tasks) { task ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = task.title, style = MaterialTheme.typography.titleMedium)
                        Text(text = task.subtitle, style = MaterialTheme.typography.bodyMedium)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            IconButton(onClick = { viewModel.toggleTask(task) }) {
                                Text(if (task.isDone) "✅" else "⬜")
                            }
                            IconButton(onClick = { viewModel.deleteTask(task) }) {
                                Text("❌")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    DogCareTheme {
    }
}
