package net.ibokette

import io.ktor.server.application.*
import io.ktor.server.netty.EngineMain
import org.jetbrains.exposed.v1.jdbc.Database

fun main(args: Array<String>) {

    EngineMain.main(args)
}

fun Application.module() {
    Database.connect(
        url = "jdbc:postgresql://localhost:5300/task",
        driver = "org.postgresql.Driver",
        user = "test_user",
        password = "password"
    )

    configureRouting()
}
