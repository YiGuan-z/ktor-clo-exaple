package com.cqsd.plugins

import com.cqsd.route.demoRoutes
import com.cqsd.route.loginRoutes
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        route("/user") {
            loginRoutes()
        }
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
