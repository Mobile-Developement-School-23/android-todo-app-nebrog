package com.example.todoapp.data.di

import com.example.todoapp.data.synchronize.RevisionHolder
import javax.inject.Qualifier

/**
 * Qualifier для класса [RevisionHolder],
 * который хранит локальную ревизию
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class LocalRevision

/**
 * Qualifier для класса [RevisionHolder],
 * который хранит последнюю известную удаленную ревизию
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RemoteRevision

/**
 * Диспатчер с единственным IO-потоком
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class LimitedDispatcher
