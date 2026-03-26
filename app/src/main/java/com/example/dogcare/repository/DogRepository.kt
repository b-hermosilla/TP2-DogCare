package com.example.dogcare.repository

import com.example.dogcare.data.DogTask
import com.example.dogcare.data.DogTaskDao
import kotlinx.coroutines.flow.Flow

class DogRepository(private val dao: DogTaskDao) {

    val allTasks: Flow<List<DogTask>> = dao.getAllTasks()

    fun getTasksByStatus(isDone: Boolean): Flow<List<DogTask>> {
        return dao.getTasksByStatus(isDone)
    }

    suspend fun insert(task: DogTask) {
        dao.insert(task)
    }

    suspend fun delete(task: DogTask) {
        dao.delete(task)
    }
    suspend fun update(task: DogTask) {
        dao.update(task)
    }
}