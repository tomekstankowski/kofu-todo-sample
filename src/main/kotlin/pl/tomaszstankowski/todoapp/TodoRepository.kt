package pl.tomaszstankowski.todoapp

import org.springframework.data.r2dbc.core.DatabaseClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Instant
import java.util.*

class TodoRepository(private val client: DatabaseClient) {

    fun insert(todo: Todo): Mono<Void> =
        client.insert()
            .into("todo")
            .value("id", todo.id)
            .value("timestamp", todo.timestamp)
            .value("text", todo.data.text)
            .then()

    fun findAll(): Flux<Todo> =
        client.execute("SELECT * FROM todo")
            .map { row ->
                Todo(
                    id = row.get("id", UUID::class.java)!!,
                    timestamp = row.get("timestamp", Instant::class.java)!!,
                    data = TodoData(
                        text = row.get("text", String::class.java)!!
                    )
                )
            }
            .all()
}