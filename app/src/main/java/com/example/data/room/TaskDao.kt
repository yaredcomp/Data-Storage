package com.example.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Module 2: Data Access Object (DAO) for the SQLite Task Database.
 *
 * Provides typed methods for querying, filtering, inserting, updating,
 * and deleting structured rows in the SQLite database.
 */
@Dao
interface TaskDao {

    /**
     * Query all tasks ordered by ID descending.
     * Returning a Flow allows Room to notify observers automatically whenever the table changes.
     */
    @Query("SELECT * FROM task_table ORDER BY id DESC")
    fun getAllTasksFlow(): Flow<List<Task>>

    /**
     * Standard suspend function query matching the chapter specification.
     */
    @Query("SELECT * FROM task_table ORDER BY id DESC")
    suspend fun getAllTasks(): List<Task>

    /**
     * Search tasks by matching title or description keyword.
     */
    @Query("""
        SELECT * FROM task_table 
        WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' 
        ORDER BY id DESC
    """)
    fun searchTasks(query: String): Flow<List<Task>>

    /**
     * Query tasks filtered by category.
     */
    @Query("SELECT * FROM task_table WHERE category = :category ORDER BY id DESC")
    fun getTasksByCategory(category: String): Flow<List<Task>>

    /**
     * Inserts a new task row into task_table.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTask(task: Task)

    /**
     * Updates an existing task record.
     */
    @Update
    suspend fun updateTask(task: Task)

    /**
     * Deletes a specific task row.
     */
    @Delete
    suspend fun deleteTask(task: Task)

    /**
     * Batch deletes all completed tasks from task_table.
     */
    @Query("DELETE FROM task_table WHERE isCompleted = 1")
    suspend fun clearCompletedTasks()
}
