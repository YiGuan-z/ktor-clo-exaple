package com.cqsd.route

import com.cqsd.entry.user
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.kodein.di.DI
import org.ktorm.entity.toCollection
import kotlin.collections.set

/**
 *
 * @author caseycheng
 * @date 2023/6/18-19:39
 **/
object NamesPool {
    private val pool: MutableMap<String, Int> = mutableMapOf()
    operator fun get(name: String): Int {
        var count = pool.getOrPut(name) { 0 }
        count += 1
        pool[name] = count
        return count
    }

    class Controller(override val di: DI) : DBController() {
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
                val count = NamesPool[name]
                call.respondText { count.toString() }
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