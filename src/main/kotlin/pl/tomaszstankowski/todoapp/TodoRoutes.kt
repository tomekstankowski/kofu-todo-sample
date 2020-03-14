package pl.tomaszstankowski.todoapp

import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.web.reactive.function.server.body
import org.springframework.web.reactive.function.server.router
import java.net.URI

fun todoRoutes(todoService: TodoService) = router {

    GET("/todos") { request ->
        val todos = todoService.getAllTodos()
        ok()
            .contentType(APPLICATION_JSON)
            .body(todos)
    }

    POST("/todos", contentType(APPLICATION_JSON)) { request ->
        request.bodyToMono(TodoDataRequest::class.java)
            .flatMap { todoDataRequest -> todoService.createTodo(todoDataRequest) }
            .flatMap { result ->
                when (result) {
                    is TodoCreateResult.Created ->
                        // TODO body is empty for some reason
                        created(URI.create("/todos/${result.todo.id}"))
                            .bodyValue(result.todo)
                    is TodoCreateResult.ValidationFailed -> unprocessableEntity().bodyValue(
                        mapOf(
                            "cause" to "illegal request body",
                            "errors" to result.errors
                        )
                    )
                    is TodoCreateResult.Error -> status(INTERNAL_SERVER_ERROR).build()
                }
            }
    }
}