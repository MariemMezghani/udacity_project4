package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

//Use FakeDataSource that acts as a test double to the LocalDataSource

class FakeDataSource(var reminders: MutableList<ReminderDTO> = mutableListOf()) :
    ReminderDataSource {

    private var error = false
    fun setShouldReturnError(shouldReturn: Boolean) {
        this.error = shouldReturn
    }


    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        // Return the remindersevcxvcxc/6trfcx
        if (!error) {
            // reminders null? !! nullpointerexception??
            return Result.Success(ArrayList(reminders))
        }
        return Result.Error("reminders not found")


    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        // save the reminder
        reminders?.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        TODO("return the reminder with the id")

    }

    override suspend fun deleteAllReminders() {
        // delete all the reminders
        reminders?.clear()
    }


}
