package com.udacity.project4.locationreminders.reminderslist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()
    private lateinit var dataSource: FakeDataSource
    private lateinit var viewModel: RemindersListViewModel

    @Before
    fun setUpViewModel() {
        stopKoin()
        dataSource = FakeDataSource()
        viewModel = RemindersListViewModel(ApplicationProvider.getApplicationContext(), dataSource)
    }

    @Test
    fun test_emptyList() = runBlockingTest {

        dataSource.deleteAllReminders()
        viewModel.loadReminders()

        Assert.assertThat(viewModel.showNoData.value, CoreMatchers.`is`(true))

    }

    @Test
    fun test_addReminder_showLoading() = runBlockingTest {
        mainCoroutineRule.pauseDispatcher()
        val reminder = ReminderDTO(
            title = "test",
            description = "desc",
            location = "location",
            latitude = 0.0,
            longitude = 0.0
        )

        dataSource.saveReminder(reminder)
        viewModel.loadReminders()
        Assert.assertThat(viewModel.showLoading.value, CoreMatchers.`is`(true))
        mainCoroutineRule.resumeDispatcher()
        Assert.assertThat(viewModel.showLoading.value, CoreMatchers.`is`(false))
    }

    @Test
    fun test_error_showSnackbar() = runBlockingTest {

        dataSource.setShouldReturnError(true)
        viewModel.loadReminders()
        Assert.assertThat(
            viewModel.showSnackBar.getOrAwaitValue(),
            CoreMatchers.`is`("reminders not found")
        )

    }

}