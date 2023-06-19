package com.cqsd.plugins

import com.cqsd.route.demoRoutes
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        authenticate {
            route("/demo") {
                demoRoutes()
            }
        }
        route("/test") {
            demoRoutes()
        }
    }
}
