package com.cqsd.route

import com.cqsd.dao.user
import com.cqsd.service.DemoRoutesService
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.kodein.di.DI
import org.kodein.di.instance
import org.ktorm.entity.toCollection

/**
 *
 * @author caseycheng
 * @date 2023/6/18-19:39
 **/
object DemoRoutes {
    class Controller(override val di: DI) : DBController() {
        private val service:DemoRoutesService by di.instance()

        override fun Route.registerRoutes() {
            authenticate {
                ping()
            }
            demoIncrement()
        }

        private fun Route.ping() {
            get<Routes.Ping> {
                val users = db.user.toCollection(mutableListOf())
                call.respond(status = HttpStatusCode.OK, message = users)
            }
        }

        private fun Route.demoIncrement() {
            get<Routes.PingCounter> {
                val name = it.name
                val counter = service.nameIncr(name)
                call.respondText { counter.toString() }
            }
        }

    }

    object Routes {
        @Resource("/ping")
        object Ping

        @Resource("/ping/{name}/count")
        data class PingCounter(val name: String)
    }
}