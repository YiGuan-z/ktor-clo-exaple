package com.cqsd

import com.cqsd.di.bindSingleton
import com.cqsd.di.exampleApp
import com.cqsd.route.LoginRoutes
import com.cqsd.route.NamesPool
import io.ktor.server.application.*
import io.ktor.server.cio.*

fun main(args: Array<String>) {
    EngineMain.main(args)
}
@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module(){
    exampleApp { application ->
        bindSingleton { application.log }
        bindSingleton { NamesPool.Controller(it) }
        bindSingleton { LoginRoutes.Controller(it) }
    }
}
