package com.cqsd.route

import com.cqsd.module.UserLoginRequest
import com.cqsd.plugins.ktorm.adaptor.DatabaseManager
import com.cqsd.plugins.ktorm.adaptor.database
import com.cqsd.service.UserService
import com.cqsd.service.user
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import org.ktorm.dsl.eq
import org.ktorm.entity.find

/**
 *
 * @author caseycheng
 * @date 2023/6/19-13:44
 **/
fun Route.loginRoutes() {
//    val note = DatabaseManager.getDataBase("note")
//    val test = DatabaseManager.getDataBase("test")

    post("/login") {
        val userDataBase = application.database.user
        flowOf(call.receive<UserLoginRequest>())
            .flowOn(Dispatchers.IO)
            .map { userLoginRequest ->
                userDataBase.find { it.id eq userLoginRequest.id }?.let { user ->
                    if (userLoginRequest.age == user.age) {
                        val token = UserService.createJWT(user)
                        return@map mapOf("token" to token)
                    }
                    null
                }
            }.collect {
                if (it != null) {
                    call.respond(it)
                } else {
                    call.respondText(text = "登陆失败", status = HttpStatusCode.Unauthorized)
                }
            }
    }
}

