package com.example.todoapp.data.alarm

import android.app.AlarmManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.AlarmManagerCompat
import androidx.core.content.getSystemService
import com.example.todoapp.data.alarm.AlarmIntentUtils.createAlarmIntent
import com.example.todoapp.data.alarm.AlarmIntentUtils.createEditIntent
import com.example.todoapp.data.alarm.AlarmIntentUtils.deleteAlarmIntent
import com.example.todoapp.domain.DeadlineManager
import com.example.todoapp.domain.TodoItem
import com.example.todoapp.utils.checkPermissions
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

class AlarmDeadlineManager @Inject constructor(
    private val context: Context,
) : DeadlineManager {

    private val alarmManager = requireNotNull(context.getSystemService<AlarmManager>())

    override fun setAlarm(todoItem: TodoItem) {
        // Проверка, что дедлайн вообще нужен
        val deadline = todoItem.deadline
        if (deadline == null || todoItem.doneFlag) {
            cancelAlarm(todoItem.itemID)
            return
        }

        // Проверка, что пермишены есть
        val isGranted = context.checkPermissions(getRequiredPermissions())
        if (!isGranted) {
            return
        }

        // Проверка, что дедлайн ещё не просрочен
        val triggerDate = getTriggerDate(deadline)
        if (triggerDate <= Calendar.getInstance().timeInMillis) {
            return
        }

        val alarmIntent = createAlarmIntent(context, todoItem.itemID, todoItem)
        val editIntent = createEditIntent(context, todoItem.itemID)

        try {
            AlarmManagerCompat.setAlarmClock(alarmManager, triggerDate, editIntent, alarmIntent)
            Log.i("nebrog", "Заметка ${todoItem.itemID} успешно запланирована на [$deadline].")
        } catch (e: SecurityException) {
            Log.e("nebrog", "Пользователь не выдал пермишены", e)
        }
    }

    override fun cancelAlarm(itemId: String) {
        val alarmIntent = deleteAlarmIntent(context, itemId)
        if (alarmIntent != null) {
            alarmManager.cancel(alarmIntent)
            Log.i("nebrog", "Заметка $itemId успешно отменена.")
        }
    }

    private fun getTriggerDate(deadline: Date): Long {
        val triggerCalendar = Calendar.getInstance().apply {
            // Выставляем желаемую дату
            time = deadline
            // Переводим часы на HH:MM:00.000
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return triggerCalendar.timeInMillis
    }

    override fun getRequiredPermissions(): List<String> {
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> listOf(
                android.Manifest.permission.POST_NOTIFICATIONS,
            )
            else -> emptyList()
        }
    }
}
