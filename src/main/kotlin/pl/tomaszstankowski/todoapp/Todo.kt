package pl.tomaszstankowski.todoapp

import java.time.Instant
import java.util.*

data class Todo(val id: UUID, val timestamp: Instant, val data: TodoData)

data class TodoData(val text: String)