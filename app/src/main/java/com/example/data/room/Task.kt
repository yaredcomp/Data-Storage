package com.example.data.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Module 2: Practical Room Entity for a Structured Task/Todo Database.
 *
 * Demonstrates how SQLite tables support multiple columns with distinct data types,
 * default values, and relational structure far beyond simple key-value pairs.
 */
@Entity(tableName = "task_table")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "description", defaultValue = "''")
    val description: String = "",

    @ColumnInfo(name = "category", defaultValue = "'Study'")
    val category: String = "Study", // Study, Work, Personal, Urgent

    @ColumnInfo(name = "priority", defaultValue = "'Medium'")
    val priority: String = "Medium", // High, Medium, Low

    @ColumnInfo(name = "dueDate", defaultValue = "''")
    val dueDate: String = "", // e.g. "Today", "Tomorrow", "Friday"

    @ColumnInfo(name = "isCompleted", defaultValue = "0")
    val isCompleted: Boolean = false,

    @ColumnInfo(name = "createdAt", defaultValue = "0")
    val createdAt: Long = System.currentTimeMillis()
)
