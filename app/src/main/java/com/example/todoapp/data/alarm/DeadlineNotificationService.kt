package com.example.todoapp.data.alarm

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.todoapp.R
import com.example.todoapp.data.alarm.AlarmIntentUtils.createEditIntent
import com.example.todoapp.data.alarm.AlarmIntentUtils.getItemId
import com.example.todoapp.data.alarm.AlarmIntentUtils.getItemName
import com.example.todoapp.data.alarm.AlarmIntentUtils.getItemPriority
import com.example.todoapp.domain.TodoItem

class DeadlineNotificationService : Service() {

    private val notificationManager by lazy { NotificationManagerCompat.from(this) }

    override fun onCreate() {
        Log.v("nebrog", "Запуск сервиса для создания нотификации")
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        handleAlarmIntent(intent)
        stopSelf(startId)
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun handleAlarmIntent(intent: Intent?) {
        val name = intent?.getItemName()
        val itemId = intent?.getItemId()
        val priority = intent?.getItemPriority()
        if (itemId != null && priority != null && name != null) {
            setUpNotification(itemId, name, priority)
        } else {
            Log.w("nebrog", "Бракованный интент какой-то [${intent?.extras}]")
        }
    }

    private fun setUpNotification(itemId: String, name: String, priority: TodoItem.Priority) {
        val activityIntent = createEditIntent(this, itemId)
        val notification = NotificationCompat.Builder(this, ALARM_CHANNEL_ID)
            .setSmallIcon( when (priority) {
                TodoItem.Priority.LOW -> R.drawable.low_priority
                TodoItem.Priority.NORMAL -> R.drawable.info
                TodoItem.Priority.HIGH -> R.drawable.high_priority
            })
            .setContentTitle(name)
            .setContentText(
                when (priority) {
                    TodoItem.Priority.LOW -> this.getString(R.string.notification_low_priority)
                    TodoItem.Priority.NORMAL -> this.getString(R.string.notification_normal_priority)
                    TodoItem.Priority.HIGH -> this.getString(R.string.notification_high_priority)
                }
            )
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(activityIntent)
            .build()

        try {
            startForeground(itemId.hashCode(), notification)
            stopForeground(STOP_FOREGROUND_DETACH)
        } catch (e: SecurityException) {
            Log.w("nebrog", "Пользователь не выдал пермишены. Очень жаль.")
            return
        }

        Log.v("nebrog", "Нотификашка успешно доставлена в трей.")
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannelCompat
            .Builder(ALARM_CHANNEL_ID, NotificationManagerCompat.IMPORTANCE_HIGH)
            .setVibrationEnabled(true)
            .setName("Дедлайны задач")
            .build()
        notificationManager.createNotificationChannel(channel)
    }

    companion object {
        private const val ALARM_CHANNEL_ID = "alarm_channel_id"
    }
}
