package com.cqsd

import com.cqsd.di.bindSingleton
import com.cqsd.di.exampleApp
import com.cqsd.route.LoginRoutes
import com.cqsd.route.NamesPool
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*

fun main(args: Array<String>) = EngineMain.main(args)

//fun main() {
//    embeddedServer(CIO, port = 8080, module = Application::module).start(wait = true)
//}

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    exampleApp { application ->
        bindSingleton { application.log }
        bindSingleton { NamesPool.Controller(it) }
        bindSingleton { LoginRoutes.Controller(it) }
    }
}
