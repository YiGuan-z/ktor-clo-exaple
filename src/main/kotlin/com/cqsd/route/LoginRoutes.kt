package com.cqsd.route

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.cqsd.entry.User
import com.cqsd.entry.user
import com.cqsd.plugins.JwtConfig
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import org.kodein.di.DI
import org.ktorm.dsl.eq
import org.ktorm.entity.find
import java.util.*

/**
 *
 * @author caseycheng
 * @date 2023/6/19-13:44
 **/

object LoginRoutes {
    class Controller(override val di: DI) : DBController() {
        override fun Route.registerRoutes() {
            loginRoutes()
        }

        private fun Route.loginRoutes() {
            post<RequestObj.UserLoginRequest>(path = "/login") { req ->
                flowOf(req)
                    .flowOn(Dispatchers.IO)
                    .map { loginRequest ->
                        db.user.find { u -> u.id eq loginRequest.id }?.let { user ->
                            if (loginRequest.age == user.age) {
                                val token = Service.createJWT(user)
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
    }

    object Service {
        @JvmStatic
        fun createJWT(user: User, millis: Long = 60000): JwtToken {
            return JWT.create()
                .withAudience(JwtConfig.audience)
                .withIssuer(JwtConfig.issuer)
                .withClaim("userId", user.id)
                .withExpiresAt(Date(System.currentTimeMillis() + millis))
                .sign(Algorithm.HMAC256(JwtConfig.secret))
        }
    }

    object RequestObj {
        @Serializable
        data class UserLoginRequest(val id: Long, val age: Int)
    }

}

typealias JwtToken = String

