package pl.tomaszstankowski.todoapp

import org.springframework.util.IdGenerator
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Clock
import java.time.Instant

class TodoService(
    private val idGenerator: IdGenerator,
    private val clock: Clock,
    private val repo: TodoRepository
) {

    private fun validateText(text: String): List<String> =
        if (text.isBlank())
            listOf("blank")
        else emptyList()

    // TODO use arrow.kt?
    private fun validate(request: TodoDataRequest): ValidationResult<TodoData> {
        val errors = validateText(request.text)
        if (errors.isEmpty()) {
            return ValidationResult.Valid(TodoData(request.text))
        }
        return ValidationResult.NotValid(
            mapOf("text" to errors)
        )
    }

    fun createTodo(request: TodoDataRequest): Mono<TodoCreateResult> =
        Mono.fromSupplier { validate(request) }
            .flatMap { validationResult ->
                when (validationResult) {
                    is ValidationResult.Valid -> {
                        val todo = Todo(
                            id = idGenerator.generateId(),
                            timestamp = Instant.now(clock),
                            data = validationResult.data
                        )
                        repo
                            .insert(todo)
                            .map<TodoCreateResult> { TodoCreateResult.Created(todo) }
                            .onErrorResume { error -> Mono.just(TodoCreateResult.Error(error)) }
                    }
                    // TODO log error
                    is ValidationResult.NotValid -> Mono.just(
                        TodoCreateResult.ValidationFailed(validationResult.errors)
                    )
                }
            }

    fun getAllTodos(): Flux<Todo> = repo.findAll()

}

data class TodoDataRequest(val text: String)

sealed class TodoCreateResult {
    data class Created(val todo: Todo) : TodoCreateResult()
    data class ValidationFailed(val errors: ValidationErrors) : TodoCreateResult()
    data class Error(val throwable: Throwable) : TodoCreateResult()
}