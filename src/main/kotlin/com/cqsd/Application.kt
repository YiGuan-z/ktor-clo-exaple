package com.cqsd

import io.ktor.server.application.*
import com.cqsd.plugins.*
import io.ktor.server.routing.*

fun main(args: Array<String>): Unit =
    io.ktor.server.cio.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    environment.config.propertyOrNull("ktor.deployment.port")?.getString().let {
        log.info("ktor server port: $it")
    }
    configureKtorm()
    configureHTTP()
    configureSecurity()
    configureSerialization()
    configureSession()
    configureRouting()
}
