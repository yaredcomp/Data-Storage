package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.room.Task
import com.example.data.room.TaskDao
import com.example.data.room.TaskDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel for Module 2: Practical SQLite Room Todo/Task Manager
 *
 * Demonstrates managing multi-field structured records, filtering,
 * reactive Flow updates, and database coroutines in viewModelScope.
 */
class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val taskDao: TaskDao = TaskDatabase.getDatabase(application).taskDao()

    // Base reactive task list from SQLite table "task_table"
    val allTasks: StateFlow<List<Task>> = taskDao.getAllTasksFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Legacy/compatibility accessor
    val tasks: StateFlow<List<Task>> get() = filteredTasks

    // Form Inputs for practical multi-field demonstration
    private val _taskTitleInput = MutableStateFlow("")
    val taskTitleInput: StateFlow<String> = _taskTitleInput.asStateFlow()

    private val _taskDescriptionInput = MutableStateFlow("")
    val taskDescriptionInput: StateFlow<String> = _taskDescriptionInput.asStateFlow()

    private val _taskCategoryInput = MutableStateFlow("Study")
    val taskCategoryInput: StateFlow<String> = _taskCategoryInput.asStateFlow()

    private val _taskPriorityInput = MutableStateFlow("Medium")
    val taskPriorityInput: StateFlow<String> = _taskPriorityInput.asStateFlow()

    private val _taskDueDateInput = MutableStateFlow("Today")
    val taskDueDateInput: StateFlow<String> = _taskDueDateInput.asStateFlow()

    // Search and Category Filter State
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategoryFilter = MutableStateFlow("All")
    val selectedCategoryFilter: StateFlow<String> = _selectedCategoryFilter.asStateFlow()

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    // Reactive filtered tasks combining search, category, and raw database flow
    val filteredTasks: StateFlow<List<Task>> = combine(
        allTasks,
        _searchQuery,
        _selectedCategoryFilter
    ) { taskList, query, catFilter ->
        taskList.filter { task ->
            val matchesCategory = (catFilter == "All") || task.category.equals(catFilter, ignoreCase = true)
            val matchesQuery = query.isBlank() ||
                    task.title.contains(query, ignoreCase = true) ||
                    task.description.contains(query, ignoreCase = true)
            matchesCategory && matchesQuery
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Form input event handlers
    fun onTaskTitleChange(newTitle: String) {
        _taskTitleInput.value = newTitle
    }

    fun onTaskDescriptionChange(newDesc: String) {
        _taskDescriptionInput.value = newDesc
    }

    fun onTaskCategoryChange(newCategory: String) {
        _taskCategoryInput.value = newCategory
    }

    fun onTaskPriorityChange(newPriority: String) {
        _taskPriorityInput.value = newPriority
    }

    fun onTaskDueDateChange(newDueDate: String) {
        _taskDueDateInput.value = newDueDate
    }

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun onCategoryFilterChange(newFilter: String) {
        _selectedCategoryFilter.value = newFilter
    }

    /**
     * Inserts a structured multi-column task using Room's insertTask suspend function.
     */
    fun addTask() {
        val title = _taskTitleInput.value.trim()
        if (title.isBlank()) {
            _statusMessage.value = "Task title is required."
            return
        }

        viewModelScope.launch {
            val newTask = Task(
                title = title,
                description = _taskDescriptionInput.value.trim(),
                category = _taskCategoryInput.value,
                priority = _taskPriorityInput.value,
                dueDate = _taskDueDateInput.value.trim(),
                isCompleted = false,
                createdAt = System.currentTimeMillis()
            )
            taskDao.insertTask(newTask)

            // Reset form fields
            _taskTitleInput.value = ""
            _taskDescriptionInput.value = ""
            _taskDueDateInput.value = "Today"
            _statusMessage.value = "Added \"$title\" to SQLite (${newTask.category} • ${newTask.priority} Priority)"
        }
    }

    /**
     * Toggles task completion.
     */
    fun toggleTaskCompletion(task: Task) {
        viewModelScope.launch {
            val updated = task.copy(isCompleted = !task.isCompleted)
            taskDao.updateTask(updated)
            val stateText = if (updated.isCompleted) "Completed" else "Pending"
            _statusMessage.value = "Marked #${task.id} as $stateText"
        }
    }

    /**
     * Deletes a specific task.
     */
    fun deleteTask(task: Task) {
        viewModelScope.launch {
            taskDao.deleteTask(task)
            _statusMessage.value = "Deleted task #${task.id} from task_table"
        }
    }

    /**
     * Clears all completed tasks in one SQL command.
     */
    fun clearCompletedTasks() {
        viewModelScope.launch {
            taskDao.clearCompletedTasks()
            _statusMessage.value = "Cleared completed tasks from SQLite"
        }
    }

    /**
     * Seeds practical real-world tasks across diverse categories and priorities.
     */
    fun seedSampleTasks() {
        viewModelScope.launch {
            val samples = listOf(
                Task(
                    title = "Review SharedPreferences XML Spec",
                    description = "Verify key-value serialization for dark_mode and app_language flags.",
                    category = "Study",
                    priority = "High",
                    dueDate = "Today",
                    isCompleted = true
                ),
                Task(
                    title = "Build Room Task Table Schema",
                    description = "Implement @Entity, @PrimaryKey, @ColumnInfo, and Singleton Database.",
                    category = "Study",
                    priority = "High",
                    dueDate = "Today",
                    isCompleted = true
                ),
                Task(
                    title = "Deploy Firebase Cloud Session",
                    description = "Verify google-services.json placement and test email authentication.",
                    category = "Work",
                    priority = "Medium",
                    dueDate = "Tomorrow",
                    isCompleted = false
                ),
                Task(
                    title = "Team Sprint Planning Meeting",
                    description = "Prepare data storage architecture diagram for Chapter 4 review.",
                    category = "Work",
                    priority = "Low",
                    dueDate = "Friday",
                    isCompleted = false
                ),
                Task(
                    title = "Submit Mobile Dev Assignment",
                    description = "Export APK and write summary report on SQLite relational performance.",
                    category = "Urgent",
                    priority = "High",
                    dueDate = "Tomorrow",
                    isCompleted = false
                )
            )
            for (sample in samples) {
                taskDao.insertTask(sample)
            }
            _statusMessage.value = "Seeded 5 practical tasks into SQLite database!"
        }
    }

    fun clearStatusMessage() {
        _statusMessage.value = null
    }
}
