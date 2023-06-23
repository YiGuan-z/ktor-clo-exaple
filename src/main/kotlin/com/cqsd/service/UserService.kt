package com.cqsd.service

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.cqsd.module.User
import com.cqsd.module.UserDefinition
import com.cqsd.plugins.JwtConfig
import com.cqsd.plugins.ktorm.adaptor.DatabaseManager
import org.ktorm.database.Database
import org.ktorm.entity.sequenceOf
import java.util.*

/**
 *
 * @author caseycheng
 * @date 2023/6/21-20:42
 **/
typealias JwtToken = String

internal val Database.user get() = this.sequenceOf(UserDefinition)

object UserService {
    private val userDB = DatabaseManager.defaultDataBase()
    fun createJWT(user: User, millis: Long = 60000): JwtToken {
        return JWT.create()
            .withAudience(JwtConfig.audience)
            .withIssuer(JwtConfig.issuer)
            .withClaim("userId", user.id)
            .withExpiresAt(Date(System.currentTimeMillis() + millis))
            .sign(Algorithm.HMAC256(JwtConfig.secret))
    }
}