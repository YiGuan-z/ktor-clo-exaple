package com.cqsd.route

import com.cqsd.di.bindSingleton
import com.cqsd.di.exampleApp
import io.ktor.client.request.*
import io.ktor.server.testing.*
import io.ktor.util.*
import kotlin.test.Test

class ControllerTest {
    @OptIn(InternalAPI::class)
    @Test
    fun getIncr() = testApplication {
        application {
            exampleApp {
                bindSingleton { DemoRoutes.Controller(it) }
            }
        }
        val response = client.get("/ping/cc/count")
        response.content.awaitContent()
        println(response)
    }
}