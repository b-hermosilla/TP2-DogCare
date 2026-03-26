package com.example.dogcare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dogcare.data.DogTask
import com.example.dogcare.repository.DogRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DogViewModel(private val repository: DogRepository) : ViewModel() {

    val tasks: StateFlow<List<DogTask>> =
        repository.allTasks.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    fun addTask(task: DogTask) {
        viewModelScope.launch {
            repository.insert(task)
        }
    }

    fun deleteTask(task: DogTask) {
        viewModelScope.launch {
            repository.delete(task)
        }
    }

    fun updateTask(task: DogTask) {
        viewModelScope.launch {
            repository.update(task)
        }
    }

    fun getTasksByStatus(isDone: Boolean): Flow<List<DogTask>> {
        return repository.getTasksByStatus(isDone)
    }
    fun toggleTask(task: DogTask) {
        viewModelScope.launch {
            repository.update(task.copy(isDone = !task.isDone))
        }
    }
}