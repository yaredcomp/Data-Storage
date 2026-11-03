package com.example

import com.example.data.room.Task
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Local JVM Unit Tests for Chapter 4 Data Storage Models & Validation logic.
 */
class ExampleUnitTest {

    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testTaskModelAttributes() {
        val task = Task(
            id = 42,
            title = "Test SQLite Task",
            description = "Educational Room demo description",
            category = "Work",
            priority = "High",
            dueDate = "Tomorrow",
            isCompleted = false
        )
        assertEquals(42, task.id)
        assertEquals("Test SQLite Task", task.title)
        assertEquals("Educational Room demo description", task.description)
        assertEquals("Work", task.category)
        assertEquals("High", task.priority)
        assertEquals("Tomorrow", task.dueDate)
        assertFalse(task.isCompleted)

        val completed = task.copy(isCompleted = true)
        assertTrue(completed.isCompleted)
    }

    @Test
    fun testSettingsDefaultKeys() {
        assertEquals("dark_mode", com.example.data.prefs.SettingsManager.KEY_DARK_MODE)
        assertEquals("app_language", com.example.data.prefs.SettingsManager.KEY_APP_LANGUAGE)
        assertEquals("English", com.example.data.prefs.SettingsManager.DEFAULT_LANGUAGE)
        assertFalse(com.example.data.prefs.SettingsManager.DEFAULT_DARK_MODE)
    }
}
