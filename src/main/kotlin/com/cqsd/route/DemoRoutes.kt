package com.cqsd.route


import com.cqsd.plugins.ktorm.adaptor.database
import com.cqsd.service.user
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.ktorm.entity.toCollection

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
}

fun Route.demoRoutes() {
    ping()
    demoIncrement()
}

private fun Route.ping() {
    get {
        val users = application.database.user.toCollection(mutableListOf())
        call.respond(status = HttpStatusCode.OK, message = users)
    }
}

private fun Route.demoIncrement() {
    get("/{name}/count") {
        val name = call.parameters["name"] ?: "unknown"
        val count = NamesPool[name]
        call.respondText { count.toString() }
    }
}
