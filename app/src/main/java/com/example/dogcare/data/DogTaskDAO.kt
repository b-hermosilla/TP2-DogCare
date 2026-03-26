package com.example.dogcare.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DogTaskDao {

    @Query("SELECT * FROM dog_tasks")
    fun getAllTasks(): Flow<List<DogTask>>

    @Insert
    suspend fun insert(task: DogTask)

    @Delete
    suspend fun delete(task: DogTask)

    @Update
    suspend fun update(task: DogTask)

    @Query("SELECT * FROM dog_tasks WHERE isDone = :isDone")
    fun getTasksByStatus(isDone: Boolean): Flow<List<DogTask>>
}