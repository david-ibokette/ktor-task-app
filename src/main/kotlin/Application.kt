package net.ibokette

import io.ktor.server.application.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.EngineMain
import io.ktor.server.netty.Netty
import io.ktor.server.thymeleaf.Thymeleaf
import org.jetbrains.exposed.v1.jdbc.Database
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver

fun main(args: Array<String>) {

//    EngineMain.main(args)
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
    }.start(wait = true)

}

fun Application.module() {
    println("inside module")
    Database.connect(
        url = "jdbc:postgresql://localhost:5300/task",
        driver = "org.postgresql.Driver",
        user = "test_user",
        password = "password"
    )

    configureRouting()
    configureTemplating()
}

fun Application.configureTemplating() {
    println("inside configureTemplating")
    install(Thymeleaf) {
        setTemplateResolver(ClassLoaderTemplateResolver().apply {
            prefix = "templates/"
            suffix = ".html"
            characterEncoding = "utf-8"
        })
    }
}
