package com.example.todoapp.data

import android.util.Log
import com.example.todoapp.data.database.OfflineRepository
import com.example.todoapp.data.di.LocalRevision
import com.example.todoapp.data.di.RemoteRevision
import com.example.todoapp.data.network.NetworkRepository
import com.example.todoapp.data.synchronize.RevisionHolder
import com.example.todoapp.di.RepositoryScope
import com.example.todoapp.domain.TodoItem
import com.example.todoapp.domain.TodoRepository
import com.example.todoapp.domain.TodoRepository.Result
import com.example.todoapp.domain.TodoRepository.Result.Failure
import com.example.todoapp.domain.TodoRepository.Result.Success
import com.example.todoapp.utils.getOr
import com.example.todoapp.utils.onFailure
import com.example.todoapp.utils.onSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

@RepositoryScope
class CacheRepository @Inject constructor(
    @RemoteRevision private val remoteRevision: RevisionHolder,
    @LocalRevision private val localRevision: RevisionHolder,
    private val remoteRepository: NetworkRepository,
    private val localRepository: OfflineRepository,
) : TodoRepository {

    override suspend fun addTodo(item: TodoItem): Result<Unit> {
        return writeCache { it.addTodo(item) }
    }

    override suspend fun deleteTodo(id: String): Result<Unit> {
        return writeCache { it.deleteTodo(id) }
    }

    override suspend fun updateTodo(item: TodoItem): Result<Unit> {
        return writeCache { it.updateTodo(item) }
    }

    override suspend fun getTodo(id: String): Result<TodoItem> {
        return readCache { it.getTodo(id) }
    }

    override suspend fun getAllTodos(): Result<List<TodoItem>> {
        return readCache { it.getAllTodos() }
    }

    override suspend fun updateAllTodos(updateList: List<TodoItem>): Result<List<TodoItem>> {
        return writeCache { it.updateAllTodos(updateList) }
    }

    override fun observeTodos(): Flow<List<TodoItem>> {
        return localRepository.observeTodos()
    }

    private suspend fun <T> readCache(action: suspend (TodoRepository) -> Result<T>): Result<T> {
        return withContext(Dispatchers.IO) {
            checkCache()
            val remoteResult = action(remoteRepository)
            when (remoteResult) {
                is Success -> remoteResult
                is Failure -> action(localRepository)
            }
        }
    }

    private suspend fun <T> writeCache(action: suspend (TodoRepository) -> Result<T>): Result<T> {
        return withContext(Dispatchers.IO) {
            checkCache()
            val writeRemoteResult = action(remoteRepository)
            when (writeRemoteResult) {
                is Success -> action(localRepository).onSuccess {
                    localRevision.setRevision(remoteRevision.getRevision())
                }
                is Failure -> action(localRepository).onSuccess {
                    Log.i("KekPek", "Не удалось выполнить операцию изменения на бекенде, сохраняем локально")
                    localRevision.setRevision(remoteRevision.getRevision() + 1)
                }
            }
        }
    }

    private suspend fun checkCache() {
        val lastKnownRemoteRev = remoteRevision.getRevision()
        // Получаем актуальные данные и актуальный номер ревизии
        val lastRemoteTodos = remoteRepository.getAllTodos().getOr { return }
        val remoteRev = remoteRevision.getRevision()
        val localRev = localRevision.getRevision()
        when {
            localRev > remoteRev -> {
                Log.i("KekPek", "Локальный ревижн обгоняет бекенд ($localRev > $remoteRev), накатываем изменения на бекенд")
                val items = localRepository.getAllTodos().getOr { return }
                remoteRepository.updateAllTodos(items)
                    .onSuccess { Log.i("KekPek", "Бекенд успешно обновлён локальными изменениями") }
                    .onFailure { Log.i("KekPek", "Не удалось обновить бекенд") }
            }
            localRev < remoteRev -> {
                Log.i("KekPek", "Локальный ревижн менше бекенда ($localRev < $remoteRev), накатываем изменения на локальное хранилище")
                localRepository.updateAllTodos(lastRemoteTodos)
            }
            lastKnownRemoteRev < remoteRev -> {
                Log.i("KekPek", "Пока приложение было оффлайн ревижн бекенда был изменён ($lastKnownRemoteRev -> $remoteRev), накатываем изменения на локальное хранилище")
                localRepository.updateAllTodos(lastRemoteTodos)
            }
            else -> {
                Log.i("KekPek", "Ревизии совпадают ($localRev = $remoteRev), действий не требуется")
            }
        }
        localRevision.setRevision(remoteRevision.getRevision())
    }
}
