package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

//Use FakeDataSource that acts as a test double to the LocalDataSource

class FakeDataSource(var reminders: MutableList<ReminderDTO>? = mutableListOf()) :
    ReminderDataSource {

    private var error = false
    fun setShouldReturnError(shouldReturn: Boolean) {
        this.error = shouldReturn
    }


    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        // Return the remindersevcxvcxc/6trfcx
        if (error) {
            return Result.Error("reminders not found")
        }
        reminders?.let { return Result.Success(ArrayList(it)) }
        return Result.Error(Exception("not found").toString())
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        // save the reminder
        reminders?.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        if (error) {
            return Result.Error("error")
        } else {
            // get reminder by id
            val reminder = reminders?.find { it.id == id }
            reminder?.let { return Result.Success(reminder) }
        }
        return Result.Error("reminder not found")
    }

    override suspend fun deleteAllReminders() {
        // delete all the reminders
        reminders?.clear()
    }


}
