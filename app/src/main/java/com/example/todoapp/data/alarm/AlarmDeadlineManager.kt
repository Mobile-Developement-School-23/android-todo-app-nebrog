package com.example.todoapp.data.alarm

import android.app.AlarmManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.AlarmManagerCompat
import androidx.core.content.getSystemService
import com.example.todoapp.data.alarm.AlarmIntentUtils.createAlarmIntent
import com.example.todoapp.data.alarm.AlarmIntentUtils.createEditIntent
import com.example.todoapp.domain.DeadlineManager
import com.example.todoapp.domain.TodoItem
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

class AlarmDeadlineManager @Inject constructor(
    private val context: Context,
) : DeadlineManager {

    private val alarmManager = requireNotNull(context.getSystemService<AlarmManager>())

    override fun setAlarm(todoItem: TodoItem) {
        val deadline = todoItem.deadline
        if (deadline == null) {
            cancelAlarm(todoItem.itemID)
            return
        }
        val triggerDate = getTriggerDate(deadline)
        if (triggerDate <= Calendar.getInstance().timeInMillis) {
            // Дедлайн уже прошел, нечего планировать
            return
        }

        val alarmIntent = createAlarmIntent(context, todoItem.itemID, todoItem)
        val editIntent = createEditIntent(context, todoItem.itemID)

        AlarmManagerCompat.setAlarmClock(alarmManager, triggerDate, editIntent, alarmIntent)
        Log.i("nebrog", "Заметка ${todoItem.itemID} успешно запланирована.")
    }

    override fun cancelAlarm(itemId: String) {
        val alarmIntent = createAlarmIntent(context, itemId)
        alarmManager.cancel(alarmIntent)
        Log.i("nebrog", "Заметка $itemId успешно отменена.")
    }

    private fun getTriggerDate(deadline: Date): Long {
        val triggerCalendar = Calendar.getInstance().apply {
            // Выставляем желаемую дату
            time = deadline
            // Переводим часы на 00:00
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return triggerCalendar.timeInMillis
    }

    override fun getRequiredPermissions(): List<String> {
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> listOf(
                android.Manifest.permission.POST_NOTIFICATIONS,
                android.Manifest.permission.SCHEDULE_EXACT_ALARM,
            )
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> listOf(
                android.Manifest.permission.SCHEDULE_EXACT_ALARM,
            )
            else -> emptyList()
        }
    }
}
