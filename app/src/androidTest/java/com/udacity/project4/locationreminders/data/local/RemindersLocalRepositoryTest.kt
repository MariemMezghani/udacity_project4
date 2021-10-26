package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.*
import org.junit.runner.RunWith
import java.util.*

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var repository: RemindersLocalRepository //object under test
    private lateinit var database: RemindersDatabase

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()
        repository = RemindersLocalRepository(database.reminderDao(), Dispatchers.Main)
    }

    @After
    fun closeDB() {
        database.close()
    }


    @Test
    fun test_saveReminder_getCorrectReminder() = runBlocking {
        // Given
        val data = ReminderDTO(
            title = null,
            description = "test",
            location = "location",
            latitude = 0.0,
            longitude = 0.0,
            id = UUID.randomUUID().toString()
        )
        //when
        repository.saveReminder(data)
        val reminder = repository.getReminder(data.id) as Result.Success
        //Then
        Assert.assertThat(reminder.data.title, `is`(data.title))

    }

    @Test
    fun test_getNonExistingReminder_returnError() = runBlocking {
        //when
        val reminder = repository.getReminder("id") as Result.Error
        // Then
        MatcherAssert.assertThat(reminder.message, Matchers.`is`("Reminder not found!"))
    }
}