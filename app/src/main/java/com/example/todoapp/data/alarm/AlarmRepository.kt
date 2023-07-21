package com.example.todoapp.data.alarm

import com.example.todoapp.data.database.OfflineRepository
import com.example.todoapp.di.RepositoryScope
import com.example.todoapp.domain.TodoItem
import com.example.todoapp.domain.TodoRepository
import com.example.todoapp.utils.getOr
import com.example.todoapp.utils.onFailure
import com.example.todoapp.utils.onSuccess
import javax.inject.Inject

@RepositoryScope
class AlarmRepository @Inject constructor(
    private val alarmDeadlineManager: AlarmDeadlineManager,
    private val repository: OfflineRepository,
) : TodoRepository by repository {

    override suspend fun addTodo(item: TodoItem): TodoRepository.Result<Unit> {
        return repository.addTodo(item).onSuccess {
            alarmDeadlineManager.setAlarm(item)
        }
    }

    override suspend fun updateTodo(item: TodoItem): TodoRepository.Result<Unit> {
        return repository.updateTodo(item).onSuccess {
            alarmDeadlineManager.setAlarm(item)
        }
    }

    override suspend fun deleteTodo(id: String): TodoRepository.Result<Unit> {
        return repository.deleteTodo(id).onSuccess {
            alarmDeadlineManager.cancelAlarm(id)
        }
    }

    override suspend fun updateAllTodos(updateList: List<TodoItem>): TodoRepository.Result<List<TodoItem>> {
        val oldItems = repository.getAllTodos()
        val result = repository.updateAllTodos(updateList).onFailure { return it }
        val newItems = repository.getAllTodos().getOr { return result }.associateBy { it.itemID }

        val oldIds = oldItems.getOr { return result }.map { it.itemID }.toSet()
        val newIds = newItems.keys.toSet()

        val idsToDelete = oldIds - newIds
        val idsToCreate = newIds - oldIds
        val idsToUpdate = newIds.intersect(oldIds)

        for (idToDelete in idsToDelete) {
            alarmDeadlineManager.cancelAlarm(idToDelete)
        }

        for (idToCreate in idsToCreate) {
            val item = newItems.getValue(idToCreate)
            alarmDeadlineManager.setAlarm(item)
        }

        for (idToUpdate in idsToUpdate) {
            val item = newItems.getValue(idToUpdate)
            alarmDeadlineManager.setAlarm(item)
        }

        return result
    }
}
