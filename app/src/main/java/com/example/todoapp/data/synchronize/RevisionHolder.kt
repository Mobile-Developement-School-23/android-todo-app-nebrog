package com.example.todoapp.data.synchronize

import android.content.SharedPreferences
import com.example.todoapp.data.di.LimitedDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

/**
 * Класс, который персистентно хранит ревизию
 */
class RevisionHolder(
    private val key: String,
    private val preferences: SharedPreferences,
    @LimitedDispatcher private val workDispatcher: CoroutineDispatcher,
) {

    suspend fun setRevision(revision: Int) {
        withContext(workDispatcher) {
            preferences.edit().putInt(key, revision).commit()
        }
    }

    suspend fun getRevision(): Int {
        return withContext(workDispatcher) {
            preferences.getInt(key, 0)
        }
    }
}
