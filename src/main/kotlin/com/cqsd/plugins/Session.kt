package com.cqsd.plugins

import io.ktor.server.application.*
import io.ktor.server.sessions.*
import kotlin.collections.set

/**
 *
 * @author caseycheng
 * @date 2023/6/18-19:13
 **/
fun Application.configureSession() {
    data class MySession(val count: Int = 0)
    install(Sessions) {
        cookie<MySession>("MY_SESSION") {
            cookie.extensions["SameSite"] = "lax"
        }
    }
}