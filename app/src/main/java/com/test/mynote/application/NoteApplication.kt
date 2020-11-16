package com.test.mynote.application

import android.app.Application
import com.test.mynote.database.NoteRoomDatabase
import com.test.mynote.repository.NoteRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class NoteApplication  : Application(){
    // No need to cancel this scope as it'll be torn down with the process
    val applicationScope = CoroutineScope(SupervisorJob())

    // Using by lazy so the database and the repository are only created when they're needed
    // rather than when the application starts
}