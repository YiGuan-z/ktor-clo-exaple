package com.cqsd.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.MissingFieldException

/**
 * @author caseycheng
 * @date 2023/7/9 02:27
 * @doc
 */
@OptIn(ExperimentalSerializationApi::class)
fun Application.configureStatusPage(callback: StatusPagesConfig.() -> Unit = {}) {
    install(StatusPages) {
        exception<BadRequestException> { call, cause ->
            cause.backtracking<MissingFieldException> { missing ->
                val field = missing.missingFields.first()
                call.respondText(text = "missing args for $field", status = HttpStatusCode.BadRequest)
            } ?: return@exception

            call.respondText(text = "未知错误🙅，原因是$cause")
        }
        callback(this)
    }
}

suspend inline fun <reified Exception : Throwable> Throwable.backtracking(
    noinline handler: suspend (cause: Exception) -> Unit
): Throwable? {
    val save = this
    var throwable: Throwable? = this
    do {
        if (throwable == null) break

        if (throwable is Exception) {
            handler(throwable)
            return null
        }
        throwable = throwable.cause
    } while (throwable != null)
    return save
}