package com.example.todoapp.data.alarm

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.todoapp.App
import com.example.todoapp.R
import com.example.todoapp.data.alarm.AlarmIntentUtils.getItemId
import com.example.todoapp.data.alarm.DeadlineNotificationChannel.ALARM_CHANNEL_ID
import com.example.todoapp.domain.TodoRepository
import com.example.todoapp.domain.TodoRepository.Result
import com.example.todoapp.utils.getOr
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

/**
 * Переносит указанную задачу на 24 часа вперёд. На время своей
 * работы поднимает нотификашку, чтобы не быть убитым системой.
 */
class DeadlinePostponeService : Service() {

    @Inject
    lateinit var repository: TodoRepository

    private val scope = CoroutineScope(Dispatchers.IO)
    private val notificationManager by lazy { NotificationManagerCompat.from(this) }

    override fun onCreate() {
        Log.i("nebrog", "Запуск сервиса для откладывания дедлайна.")
        (application as App).appComponent.getAlarmServiceComponentFactory().create().inject(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        handlePostponeIntent(intent, startId)
        return START_STICKY
    }

    override fun onDestroy() {
        scope.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun handlePostponeIntent(intent: Intent?, startId: Int) {
        if (intent == null) {
            stopSelfResult(startId)
            return
        }

        val itemId = intent.getItemId()
        val action = intent.action
        if (itemId == null || action != POSTPONE_ACTION) {
            Log.w("nebrog", "Бракованный интент какой-то [${intent.extras}, $action]")
            stopSelfResult(startId)
            return
        }

        // Поднимаем нотификашку
        setUpForegroundNotification(itemId)
        scope.launch {
            // Уходим на другой тред, отпускаем сервис
            val result = postponeAlarm(itemId)
            when (result) {
                is Result.Success -> {
                    Log.i("nebrog", "Нотификашка [$itemId] успешно отложена")
                    stopForeground(STOP_FOREGROUND_REMOVE)
                }

                is Result.Failure -> {
                    Log.w("nebrog", "Нотификашку [$itemId] не удалось отложить [${result.message}]")
                    setUpErrorNotification(itemId)
                    stopForeground(STOP_FOREGROUND_DETACH)
                }
            }
            // Останавливаем сервис
            stopSelfResult(startId)
            Log.i("nebrog", "Сервис закончил работу по нотификашке [$itemId]")
        }
    }

    private suspend fun postponeAlarm(itemId: String): Result<Unit> {
        val todoItem = repository.getTodo(itemId).getOr { return it }
        val deadline = todoItem.deadline
        if (deadline == null) {
            Log.w("nebrog", "Дедлайна уже нет, это странно.")
            return Result.Success(Unit)
        }
        val postponedDeadline = deadline.time + POSTPONE_TIME_MILLIS
        val postponedTodoItem = todoItem.copy(deadline = Date(postponedDeadline))
        return repository.updateTodo(postponedTodoItem)
    }

    private fun setUpForegroundNotification(itemId: String) {
        val notification = NotificationCompat.Builder(this, ALARM_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_posptpone)
            .setContentTitle(getString(R.string.notification_deadline_postpone_title))
            .setContentText(getString(R.string.notification_deadline_postpone_text))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        try {
            startForeground(itemId.hashCode(), notification)
        } catch (e: SecurityException) {
            Log.w("nebrog", "Пользователь не выдал пермишены. Очень жаль.", e)
            return
        }
    }

    private fun setUpErrorNotification(itemId: String) {
        val retryIntent = AlarmIntentUtils.createPostponeIntent(this, itemId)
        val notification = NotificationCompat.Builder(this, ALARM_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_error_filled)
            .setContentTitle(getString(R.string.notification_deadline_postpone_error_title))
            .setContentText(getString(R.string.notification_deadline_postpone_error_text))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(retryIntent)
            .build()

        try {
            notificationManager.notify(itemId.hashCode(), notification)
        } catch (e: SecurityException) {
            Log.w("nebrog", "Пользователь не выдал пермишены. Очень жаль.", e)
            return
        }
    }

    companion object {
        private const val POSTPONE_ACTION =
            "com.example.todoapp.data.alarm.DeadlinePostponeService_postpone"
        private const val POSTPONE_TIME_MILLIS = 24 * 60 * 60 * 1000 // 24 часа в миллисекундах

        fun createPostponeIntent(context: Context): Intent {
            return Intent(context, DeadlinePostponeService::class.java).apply {
                action = POSTPONE_ACTION
            }
        }
    }
}
