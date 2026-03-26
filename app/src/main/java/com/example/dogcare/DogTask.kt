package com.example.dogcare.data

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "dog_tasks")
data class DogTask(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val subtitle: String,
    val isDone: Boolean = false
)
