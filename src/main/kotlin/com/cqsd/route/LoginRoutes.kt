package com.cqsd.route

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.cqsd.module.User
import com.cqsd.module.UserLoginRequest
import com.cqsd.plugins.JwtConfig
import com.cqsd.plugins.database
import com.cqsd.plugins.user
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.ktorm.dsl.eq
import org.ktorm.entity.find
import java.util.*

/**
 *
 * @author caseycheng
 * @date 2023/6/19-13:44
 **/
fun Route.loginRoutes() {
    post("/login") {
        val user: UserLoginRequest = call.receive()
        database.user.find { it.id eq user.id }?.let {
            if (user.age == it.age) {
                val token = JWT.create()
                    .withAudience(JwtConfig.audience)
                    .withIssuer(JwtConfig.issuer)
                    .withClaim("userId", it.id)
                    .withExpiresAt(Date(System.currentTimeMillis() + 60000))
                    .sign(Algorithm.HMAC256(JwtConfig.secret))
                call.respond(mapOf("token" to token))
            }
        }
    }
}