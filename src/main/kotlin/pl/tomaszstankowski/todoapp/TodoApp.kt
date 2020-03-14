package pl.tomaszstankowski.todoapp

import org.springframework.boot.WebApplicationType
import org.springframework.fu.kofu.application
import org.springframework.fu.kofu.r2dbc.r2dbcPostgresql
import org.springframework.fu.kofu.webflux.webFlux
import org.springframework.util.IdGenerator
import org.springframework.util.JdkIdGenerator
import java.time.Clock

val app = application(type = WebApplicationType.REACTIVE) {

    beans {
        bean<Clock> { Clock.systemUTC() }
        bean<IdGenerator> { JdkIdGenerator() }
        bean<TodoRepository>()
        bean<TodoService>()
        bean(::todoRoutes)
    }

    webFlux {
        codecs {
            jackson {
            }
        }
    }

    // TODO configure via application.yml
    r2dbcPostgresql {
        database = "todo"
        host = "localhost"
        port = 5432
        username = "admin"
        password = "admin"
    }


}

fun main() {
    app.run()
}
