package net.ibokette

import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.http.content.staticResources
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.request.receiveParameters
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.thymeleaf.ThymeleafContent
import net.ibokette.model.Priority
import net.ibokette.model.Task
import net.ibokette.model.TaskRepository
import net.ibokette.model.tasksAsMap
import net.ibokette.model.tasksAsTable

fun Application.configureRouting() {
    routing {
        install(StatusPages) {
            exception<IllegalStateException> { call, cause ->
                call.respondText("App in illegal state as ${cause.message}")
            }
        }

        staticResources("/content", "mycontent")
        staticResources("/task-ui", "task-ui")

        route("/th-tasks") {
            get {
//                val all = TaskRepository.allTasksFromDB().tasksAsMap()
//                val t = Task("a", "asaa", Priority.High, false)
//                val mymap: Map<String, List<Task>> = mapOf("taskList", listOf(t))

                call.respond(ThymeleafContent("tasks",mapOf("taskList" to TaskRepository.allTasksFromDB())))
//                call.respond(ThymeleafContent("task",mapOf("taskList", listOf(t))))
            }
        }
        route("/tasks") {
            get {
                call.respondText(
                    contentType = ContentType.parse("text/html"),
                    text = TaskRepository.allTasksFromDB().tasksAsTable()
                )
            }

            //add the following route
            get("/byPriority/{priority?}") {
                val priorityAsText = call.parameters["priority"]
                if (priorityAsText == null) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@get
                }

                try {
                    val priority = Priority.valueOf(priorityAsText)
                    val tasks = TaskRepository.tasksByPriorityFromDB(priority)

                    if (tasks.isEmpty()) {
                        call.respond(HttpStatusCode.NotFound)
                        return@get
                    }

                    call.respondText(
                        contentType = ContentType.parse("text/html"),
                        text = tasks.tasksAsTable()
                    )
                } catch(ex: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }

            post {
                val formContent = call.receiveParameters()

                val params = Triple(
                    formContent["name"] ?: "",
                    formContent["description"] ?: "",
                    formContent["priority"] ?: ""
                )

                if (params.toList().any { it.isEmpty() }) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }

                try {
                    val priority = Priority.valueOf(params.third)
                    TaskRepository.addTaskToDB(
                        Task(
                            params.first,
                            params.second,
                            priority
                        )
                    )

                    call.respond(HttpStatusCode.NoContent)
                } catch (ex: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest)
                } catch (ex: IllegalStateException) {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }
        }

        /////////////////////////////////////////////
        // From the previous tutorial
        /////////////////////////////////////////////

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
        get("/thanos") {
            println("Thanos has been activated!")
            call.respond(ThymeleafContent("index", mapOf("message" to "Hello World")))
        }

    }
}
