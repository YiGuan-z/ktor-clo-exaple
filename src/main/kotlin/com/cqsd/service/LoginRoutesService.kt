package com.cqsd.service

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.cqsd.dao.User
import com.cqsd.di.App
import com.cqsd.plugins.JwtConfig
import org.kodein.di.DI
import org.kodein.di.DIAware
import java.util.*

/**
 *
 * @author caseycheng
 * @date 2023/7/10-15:15
 * @doc
 **/
interface LoginRoutesService : DIAware,Service {
    fun createJWT(user: User, millis: Long = 60000): JwtToken
    companion object Default :LoginRoutesService{
        override fun createJWT(user: User, millis: Long): JwtToken {
            return JWT.create()
                .withAudience(JwtConfig.audience)
                .withIssuer(JwtConfig.issuer)
                .withClaim("userId", user.id)
                .withExpiresAt(Date(System.currentTimeMillis() + millis))
                .sign(Algorithm.HMAC256(JwtConfig.secret))
        }
        /**
         * A DI Aware class must be within reach of a [DI] object.
         */
        override val di: DI
            get() = App.diContent
    }
}
typealias JwtToken=String