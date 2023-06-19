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

    // Please read the jwt property from the config file if you are using EngineMain
//    val jwtAudience = "jwt-audience"
//    val jwtDomain = "https://jwt-provider-domain/"
//    val jwtRealm = "ktor sample app"
//    val jwtSecret = "secret"
    val jwtSecret = environment.config.property("jwt.secret").getString()
    val jwtAudience = environment.config.property("jwt.audience").getString()
    val jwtRealm = environment.config.property("jwt.realm").getString()
    val jwtDomain = environment.config.property("jwt.domain").getString()
    val jwtIssuer = environment.config.property("jwt.issuer").getString()
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
            verifier(
                JWT
                    .require(Algorithm.HMAC256(jwtSecret))
                    .withAudience(jwtAudience)
                    .withIssuer(jwtIssuer)
                    .build()
            )
            //用于对token的验证
            validate { credential ->
                if (credential.payload.getClaim("userId").asLong() != -1L) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
            //验证失败如何返回
            challenge{ defaultScheme, realm ->
                call.respond(status = HttpStatusCode.Unauthorized, message = "Token is not valid or has expired : $realm ,$defaultScheme")
            }

        }
    }
}
