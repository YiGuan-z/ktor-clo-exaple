package com.cqsd.route

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.cqsd.di.DBController
import com.cqsd.plugins.JwtConfig
import com.cqsd.route.LoginRoutes.Data.user
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.MissingFieldException
import kotlinx.serialization.Serializable
import org.kodein.di.DI
import org.ktorm.database.Database
import org.ktorm.dsl.QueryRowSet
import org.ktorm.dsl.eq
import org.ktorm.entity.find
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.BaseTable
import org.ktorm.schema.int
import org.ktorm.schema.long
import org.ktorm.schema.varchar
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

        @OptIn(ExperimentalSerializationApi::class)
        override fun StatusPagesConfig.registerExceptionHandle() {
            exception<MissingFieldException> { call, cause ->
                val field = cause.missingFields.first()
                call.respondText(text = "missing args for $field")
            }

        }

        private fun Route.loginRoutes() {
            post<Routes.UserLoginRequest>(path = "/login") { req ->
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
        fun createJWT(user: Data.User, millis: Long = 60000): JwtToken {
            return JWT.create()
                .withAudience(JwtConfig.audience)
                .withIssuer(JwtConfig.issuer)
                .withClaim("userId", user.id)
                .withExpiresAt(Date(System.currentTimeMillis() + millis))
                .sign(Algorithm.HMAC256(JwtConfig.secret))
        }
    }

    object Routes {
        //        @Resource("/login")
        @Serializable
        data class UserLoginRequest(val id: Long, val age: Int)
        //MissingFieldException
    }

    object Data {
        internal val Database.user get() = this.sequenceOf(UserDefinition)

        //数据模型
        @Serializable
        data class User(
            val id: Long,
            var name: String,
            var age: Int,
            var email: String
        )

        //数据库声明
        object UserDefinition : BaseTable<User>("user") {
            val id = long("id").primaryKey()
            val name = varchar("name")
            val age = int("age")
            val email = varchar("email")

            override fun doCreateEntity(row: QueryRowSet, withReferences: Boolean): User {
                return User(
                    id = row[id] ?: -1,
                    name = row[name] ?: "",
                    age = row[age] ?: 0,
                    email = row[email] ?: ""
                )
            }
        }
    }
}

typealias JwtToken = String

