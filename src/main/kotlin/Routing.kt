package net.ibokette

import io.ktor.http.ContentType
import io.ktor.server.application.*
import io.ktor.server.http.content.staticResources
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.*
import io.ktor.server.routing.*
import net.ibokette.model.tasks
import net.ibokette.model.tasksAsTable

fun Application.configureRouting() {
    routing {
        install(StatusPages) {
            exception<IllegalStateException> { call, cause ->
                call.respondText("App in illegal state as ${cause.message}")
            }
        }

        staticResources("/content", "mycontent")

        get("/tasks") {
            call.respondText(
                contentType = ContentType.parse("text/html"),
                text = tasks.tasksAsTable()
            )
        }

        get("/") {
            call.respondText("Hello World!")
        }
        get("/apokolips") {
            val text = "<h1>Hello Darkseid!</h1>"
            val type = ContentType.parse("text/html")
            call.respondText(text, type)
        }
        get("/error-test") {
            throw IllegalStateException("This is a test exception")
        }
    }
}
