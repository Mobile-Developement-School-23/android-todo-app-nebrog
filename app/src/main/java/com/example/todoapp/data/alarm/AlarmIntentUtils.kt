package com.example.todoapp.data.alarm

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.PendingIntentCompat
import com.example.todoapp.domain.TodoItem
import com.example.todoapp.presentation.MainActivity

/**
 * Класс для работы с алярм-интентами (создание и последующее извлечение данных)
 */
object AlarmIntentUtils {

    private const val ITEM_ID_KEY = "TodoIntentUtils_id"
    private const val ITEM_NAME_KEY = "TodoIntentUtils_name"
    private const val ITEM_PRIORITY_KEY = "TodoIntentUtils_priority"

    fun Intent.getItemId(): String? {
        return getStringExtra(ITEM_ID_KEY)
    }

    fun Intent.getItemName(): String? {
        return getStringExtra(ITEM_NAME_KEY)
    }

    fun Intent.getItemPriority(): TodoItem.Priority? {
        val enumOrdinal = getIntExtra(ITEM_PRIORITY_KEY, -1)
        return TodoItem.Priority.values().find { it.ordinal == enumOrdinal }
    }

    /**
     * Создает интент, который вызовет сервис, который должен поднять нотификашу.
     */
    fun createAlarmIntent(context: Context, itemId: String, item: TodoItem? = null): PendingIntent {
        val serviceIntent = Intent(context, DeadlineNotificationService::class.java)
        serviceIntent.putItemId(itemId)
        if (item != null) {
            serviceIntent.putItemInformation(item)
        }
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            PendingIntentCompat.getForegroundService(
                context,
                itemId.hashCode(),
                serviceIntent,
                PendingIntent.FLAG_UPDATE_CURRENT,
                false
            )
        } else {
            PendingIntentCompat.getService(
                context,
                itemId.hashCode(),
                serviceIntent,
                PendingIntent.FLAG_UPDATE_CURRENT,
                false
            )
        }
    }

    /**
     * Создает интент, который вызовет активити, которая должна открыть экран редактирования нужной тудухи.
     */
    fun createEditIntent(context: Context, itemId: String): PendingIntent {
        val activityIntent = Intent(context, MainActivity::class.java)
        activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        activityIntent.putItemId(itemId)
        return PendingIntentCompat.getActivity(
            context,
            0,
            activityIntent,
            PendingIntent.FLAG_UPDATE_CURRENT,
            false
        )
    }

    private fun Intent.putItemId(itemId: String): Intent {
        return putExtra(ITEM_ID_KEY, itemId)
    }

    private fun Intent.putItemInformation(item: TodoItem): Intent {
        putExtra(ITEM_PRIORITY_KEY, item.itemPriority.ordinal)
        putExtra(ITEM_NAME_KEY, item.itemText)
        return this
    }
}
