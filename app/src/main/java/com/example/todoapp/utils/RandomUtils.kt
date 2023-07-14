package com.example.todoapp.utils

import com.example.todoapp.domain.TodoItem
import java.util.Calendar
import java.util.UUID
import kotlin.random.Random

private val random = Random(System.currentTimeMillis())
private var counter = 0
fun getRandomTodo(): TodoItem {
    val texts = listOf(
        "Купить хлеб",
        "Выкинуть мусор, выучить английский, написать книгу",
        "Заселить Марс, посадить лес, построить руками город, собрать машину, проплыть океан, 10 раз отжаться, позвонить в сбербанк - предложить кредитную карту, научить сальто",
    )
    val changeDate = if (random.nextBoolean()) Calendar.getInstance().time else null
    val deadline = if (random.nextBoolean()) Calendar.getInstance().time else null
    return TodoItem(
        itemID = UUID.randomUUID().toString(),
        itemText = "${texts.random(random)} ${counter++}",
        itemPriority = TodoItem.Priority.values().random(random),
        deadline = deadline,
        doneFlag = random.nextBoolean(),
        dateOfCreation = Calendar.getInstance().time,
        dateOfChanges = changeDate,
    )
}
