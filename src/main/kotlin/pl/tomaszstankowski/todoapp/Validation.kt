package pl.tomaszstankowski.todoapp

sealed class ValidationResult<T> {
    data class Valid<T>(val data: T) : ValidationResult<T>()
    data class NotValid<T>(val errors: ValidationErrors) : ValidationResult<T>()
}

typealias ValidationErrors = Map<String, List<String>>