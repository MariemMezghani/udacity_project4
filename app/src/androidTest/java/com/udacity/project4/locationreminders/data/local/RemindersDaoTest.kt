package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import com.udacity.project4.locationreminders.data.dto.ReminderDTO

import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

import kotlinx.coroutines.ExperimentalCoroutinesApi;
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Test
import java.util.*

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class ReminderDAOTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    private lateinit var database: RemindersDatabase

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).build()
    }

    @After
    fun closeDB() {
        database.close()
    }

    @Test
    fun insertReminder_getById() = runBlockingTest {
        // Given
        val data = ReminderDTO(
            title = "test",
            description = "description",
            location = "location",
            latitude = 0.0,
            longitude = 0.0,
            id = UUID.randomUUID().toString()
        )
        database.reminderDao().saveReminder(data)
        //When - get reminder by id
        val loaded = database.reminderDao().getReminderById(data.id)
        // Then - loaded contains correct values
        assertThat<ReminderDTO>(loaded as ReminderDTO, notNullValue())
        assertThat(loaded.id, `is`(data.id))
        assertThat(loaded.id, `is`(data.id))
        assertThat(loaded.title, `is`(data.title))
        assertThat(loaded.description, `is`(data.description))
        assertThat(loaded.latitude, `is`(data.latitude))
        assertThat(loaded.longitude, `is`(data.longitude))
        assertThat(loaded.location, `is`(data.location))

    }
}