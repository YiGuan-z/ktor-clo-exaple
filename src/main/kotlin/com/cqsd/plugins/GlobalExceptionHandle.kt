package com.cqsd.plugins

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
            var th: Throwable? = cause
            do {
                if (th == null) {
                    break
                }
                if (th is MissingFieldException) {
                    val field = th.missingFields.first()
                    call.respondText(text = "missing args for $field")
                    return@exception
                }
                //(call,Exception)->Unit
                th = th.cause
            } while (th != null)
            call.respondText(text = "未知错误🙅，原因是$cause")
        }
        callback(this)
    }
}