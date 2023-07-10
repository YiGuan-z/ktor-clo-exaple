package com.cqsd.service.impl

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.cqsd.dao.User
import com.cqsd.plugins.JwtConfig
import com.cqsd.service.JwtToken
import com.cqsd.service.LoginRoutesService
import org.kodein.di.DI
import java.util.*

/**
 *
 * @author caseycheng
 * @date 2023/7/10-15:17
 * @doc
 **/
class LoginRoutesServiceImpl(override val di: DI):LoginRoutesService {
    override fun createJWT(user: User, millis: Long): JwtToken {
        return JWT.create()
            .withAudience(JwtConfig.audience)
            .withIssuer(JwtConfig.issuer)
            .withClaim("userId", user.id)
            .withExpiresAt(Date(System.currentTimeMillis() + millis))
            .sign(Algorithm.HMAC256(JwtConfig.secret))
    }
}