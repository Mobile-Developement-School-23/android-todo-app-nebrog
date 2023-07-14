package com.example.todoapp.presentation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.todoapp.R
import com.example.todoapp.data.alarm.AlarmIntentUtils.getItemId
import com.example.todoapp.presentation.editTodo.EditTodoFragment

/**
 * Single Activity приложения
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // For the case when app wasn't launched and starts from notification.
        handleAlarmIntent(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleAlarmIntent(intent)
    }

    private fun handleAlarmIntent(intent: Intent?) {
        val itemId = intent?.getItemId()
        if (itemId != null) {
            Log.v("nebrog", "Интент от нотификашки, открываем экран редактирования для [$itemId]")
            redirectToEditScreen(itemId)
        } else {
            Log.d("nebrog", "Левый интент [$intent]")
        }
    }

    private fun redirectToEditScreen(itemId: String) {
        val editTodo = EditTodoFragment.createNewInstance(itemId)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container_view, editTodo)
            .addToBackStack(null)
            .commit()
    }
}
