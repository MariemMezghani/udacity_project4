package com.udacity.project4
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import com.udacity.project4.locationreminders.RemindersActivity
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.local.LocalDB
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import com.udacity.project4.locationreminders.reminderslist.RemindersListViewModel
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.util.DataBindingIdlingResource
import com.udacity.project4.util.monitorActivity
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest

// End to end test
@RunWith(AndroidJUnit4::class)
@LargeTest
class RemindersActivityTest : AutoCloseKoinTest() {
    private lateinit var repository: ReminderDataSource
    private val dataBindingIdlingResource = DataBindingIdlingResource()

    @Rule
    @JvmField
    var activityRule: ActivityTestRule<RemindersActivity?>? =
        ActivityTestRule(RemindersActivity::class.java)


    @Before
    fun setUp() {
        stopKoin()
        val myModule = module {
            viewModel {
                RemindersListViewModel(
                    ApplicationProvider.getApplicationContext(),
                    get() as ReminderDataSource
                )
            }
            single {
                SaveReminderViewModel(
                    ApplicationProvider.getApplicationContext(),
                    get() as ReminderDataSource
                )
            }
            single { RemindersLocalRepository(get()) as ReminderDataSource }
            single { LocalDB.createRemindersDao(ApplicationProvider.getApplicationContext()) }

        }
        startKoin {
            androidContext(ApplicationProvider.getApplicationContext())
            modules(listOf(myModule))
        }
        repository = GlobalContext.get().koin.get()
        runBlocking {
            repository.deleteAllReminders()
        }
    }

    @After
    fun reset() {
        stopKoin()
    }

    @Before
    fun registerIdlingResources() {
        IdlingRegistry.getInstance().register(dataBindingIdlingResource)
    }

    @After
    fun unregisterIdlingResources() {
        IdlingRegistry.getInstance().unregister(dataBindingIdlingResource)
    }

    @Test
    fun authenticationTest_returnAuthenticatedUser() {
        val activityScenario = ActivityScenario.launch(RemindersActivity::class.java)
        activityScenario.close()
    }

    @Test
    fun test_reminderAdded_displayedUI() {

        val activityScenario = ActivityScenario.launch(RemindersActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)
        //no data
        onView(
            withId(R.id.noDataTextView)
        ).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        // Click FAB
        onView(withId(R.id.addReminderFAB)).perform(click())
        onView(withId(R.id.reminderTitle)).perform(replaceText("test"))
        onView(withId(R.id.reminderDescription))
            .perform(replaceText("description"))
        // navigate to SelectLocationFragment
        onView(withId(R.id.selectLocation)).perform(click())
        // set marker
        onView(withId(R.id.map)).perform(longClick())
        onView(withId(R.id.save_button)).perform(click())
        // save reminder
        onView(withId(R.id.saveReminder)).perform(click())
        // test Toast
        onView(withText(R.string.reminder_saved)).inRoot(
            withDecorView(
                not(
                    activityRule?.getActivity()?.getWindow()?.getDecorView()
                )
            )
        ).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        // reminder displayed
        onView(ViewMatchers.withText("test"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(ViewMatchers.withText("description"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        activityScenario.close()

    }

    @Test
    fun test_noLocation_snackbarDisplayed() {
        val activityScenario = ActivityScenario.launch(RemindersActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)
        // Click FAB and add inly title and description
        onView(withId(R.id.addReminderFAB)).perform(click())
        onView(withId(R.id.reminderTitle)).perform(replaceText("test"))
        onView(withId(R.id.reminderDescription))
            .perform(replaceText("description"))
        // click on save reminder
        onView(withId(R.id.saveReminder)).perform(click())
        // test snackbar displayed
        onView(ViewMatchers.withId(com.google.android.material.R.id.snackbar_text))
            .check(ViewAssertions.matches(withText(R.string.err_select_location)))

        activityScenario.close()
    }
}
