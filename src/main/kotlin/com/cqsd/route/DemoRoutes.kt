package com.cqsd.route

import com.cqsd.dao.user
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.kodein.di.*
import org.kodein.type.erased
import org.ktorm.entity.toCollection
import org.slf4j.Logger
import kotlin.collections.set

/**
 *
 * @author caseycheng
 * @date 2023/6/18-19:39
 **/
object DemoRoutes {
    private val pool: MutableMap<String, Int> = mutableMapOf()
    operator fun get(name: String): Int {
        var count = pool.getOrPut(name) { 0 }
        count += 1
        pool[name] = count
        return count
    }

    class Controller(override val di: DI) : DBController() {
        val log:Logger by di.instance()
        /**
         * 模块构建
         */
        override fun DI.Builder.registerModule() {
            bindConstant("userName"){"Cheng"}
        }

        override fun Route.registerRoutes() {
            val userName:String by di.constant()
            log.info("tag is {}",userName)
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
                val count = DemoRoutes[name]
                call.respondText { count.toString() }
            }
        }

        companion object Service {
            @field:JvmField
            val pool: MutableMap<String, Int> = mutableMapOf()
            operator fun get(name: String): Int {
                val counter = pool.getOrPut(name) { 0 }
                counter + 1
                pool[name] = counter
                return counter
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