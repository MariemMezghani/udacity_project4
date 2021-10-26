package com.udacity.project4.locationreminders.savereminder

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.R
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import junit.framework.TestCase

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.annotation.Config
import java.util.*

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class SaveReminderViewModelTest() : TestCase() {
    @get:Rule
    var instantTaskExecutorRule= InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()
    private lateinit var dataSource: FakeDataSource
    private lateinit var saveReminderViewModel: SaveReminderViewModel // instance under test


    @Before
    fun setUpViewModel(){
        stopKoin()
        dataSource= FakeDataSource()
        saveReminderViewModel= SaveReminderViewModel(ApplicationProvider.getApplicationContext(),dataSource)
    }

    @Test
    fun showLoading()= runBlockingTest{
        mainCoroutineRule.pauseDispatcher()
        // Given
        val data = ReminderDataItem(
            title = "test,",
            description = "description",
            location = "location",
            latitude = 0.0,
            longitude = 0.0,
            id = UUID.randomUUID().toString()
        )
        // when
        saveReminderViewModel.saveReminder(data)

        // Then
        MatcherAssert.assertThat(saveReminderViewModel.showLoading.getOrAwaitValue(), Matchers.`is`(true))
        mainCoroutineRule.resumeDispatcher()
        MatcherAssert.assertThat(saveReminderViewModel.showLoading.getOrAwaitValue(), Matchers.`is`(false))
    }

    @Test
    fun errorSnackbar() {
        // Given
        val data = ReminderDataItem(
            title = null,
            description = "test",
            location = "location",
            latitude = 0.0,
            longitude = 0.0,
            id = UUID.randomUUID().toString()
        )

        // When
        mainCoroutineRule.runBlockingTest {
            saveReminderViewModel.validateEnteredData(data)
        }

        // Then
        MatcherAssert.assertThat(
            saveReminderViewModel.showSnackBarInt.getOrAwaitValue(),
            Matchers.`is`(R.string.err_enter_title)
        )
    }

}
