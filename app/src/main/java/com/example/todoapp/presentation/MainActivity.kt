package com.example.todoapp.presentation

import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.todoapp.App
import com.example.todoapp.R
import com.example.todoapp.data.alarm.AlarmIntentUtils.getItemId
import com.example.todoapp.domain.DeadlineManager
import com.example.todoapp.presentation.editTodo.EditTodoFragment
import com.example.todoapp.presentation.permissions.PermissionsFragment
import javax.inject.Inject

/**
 * Single Activity приложения
 */
class MainActivity : AppCompatActivity() {

    @Inject lateinit var deadlineManager: DeadlineManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (this.applicationContext as App).appComponent.getMainActivityComponentFactory().create().inject(this)

        setContentView(R.layout.activity_main)

        // For the case when app wasn't launched and starts from notification.
        handleAlarmIntent(intent)
        checkPermissions()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleAlarmIntent(intent)
    }

    private fun checkPermissions() {
        val nonGrantedPermissions = ArrayList<String>()
        for (permission in deadlineManager.getRequiredPermissions()) {
            val result = ContextCompat.checkSelfPermission(this, permission)
            if (result != PERMISSION_GRANTED) {
                nonGrantedPermissions.add(permission)
            }
        }
        if (nonGrantedPermissions.isNotEmpty()) {
            redirectToPermissionsScreen(nonGrantedPermissions)
        }
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

    private fun redirectToPermissionsScreen(permissions: ArrayList<String>) {
        val permissionsFragment = PermissionsFragment.createNewInstance(permissions)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container_view, permissionsFragment)
            .addToBackStack(null)
            .commit()
    }
}
