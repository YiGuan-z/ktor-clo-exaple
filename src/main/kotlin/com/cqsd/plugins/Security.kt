package com.cqsd.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*

object JwtConfig {
    lateinit var secret: String
    lateinit var audience: String
    lateinit var realm: String
    lateinit var domain: String
    lateinit var issuer: String
}

fun Application.configureSecurity() {
    val config = environment.config.config("jwt")

    val jwtSecret = config.property("secret").getString()
    val jwtAudience = config.property("audience").getString()
    val jwtRealm = config.property("realm").getString()
    val jwtDomain = config.property("domain").getString()
    val jwtIssuer = config.property("issuer").getString()

    JwtConfig.apply {
        this.domain = jwtDomain
        this.audience = jwtAudience
        this.realm = jwtRealm
        this.secret = jwtSecret
        this.issuer = jwtIssuer
    }
    authentication {
        /*
        * 可以使用jwt(name) 保护单个作用域
        * */
        jwt {
            realm = jwtRealm
            //用于对token的解码
            //decode userToken
            verifier(
                JWT
                    .require(Algorithm.HMAC256(jwtSecret))
                    .withAudience(jwtAudience)
                    .withIssuer(jwtIssuer)
                    .build()
            )
            //validate user token content
            validate { credential ->
                if (credential.payload.getClaim("userId").asLong() != -1L) {
                    //可以在这里添加
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
            //验证失败如何返回
            challenge { defaultScheme, realm ->
                call.respond(
                    status = HttpStatusCode.Unauthorized,
                    message = "Token is not valid or has expired : $realm ,$defaultScheme"
                )
            }

        }
    }
}
