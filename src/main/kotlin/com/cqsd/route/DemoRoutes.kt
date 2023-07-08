package com.cqsd.route

import com.cqsd.di.BaseController
import com.cqsd.route.LoginRoutes.Data.user
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.kodein.di.DI
import org.kodein.di.instance
import org.ktorm.database.Database
import org.ktorm.entity.toCollection
import kotlin.collections.MutableMap
import kotlin.collections.getOrPut
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf
import kotlin.collections.set

/**
 *
 * @author caseycheng
 * @date 2023/6/18-19:39
 **/
object NamesPool {
    private val pool: MutableMap<String, Long> = mutableMapOf()
    operator fun get(name: String): Long {
        var count = pool.getOrPut(name) { 0L }
        count += 1
        pool[name] = count
        return count
    }

    class Controller(override val di: DI) : BaseController() {
        val db: Database by instance()
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