package com.cqsd.di

import com.alibaba.druid.pool.DruidDataSource
import com.cqsd.plugins.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.routing.*
import org.kodein.di.*
import org.kodein.type.jvmType
import org.ktorm.database.Database

/**
 * @author caseycheng
 * @date 2023/7/8 19:25
 * @doc
 */
lateinit var di: DI
fun Application.exampleApp(kodeinMapper: DI.MainBuilder.(Application) -> Unit = {}) {
    val application = this
    // logging
    application.configureLogging()
    //db
    val db = application.configureDB()
    //resource
    application.install(Resources)
    // http
    application.configureHTTP()
    //security
    application.configureSecurity()
    //serialization
    application.configureSerialization()

    di = DI {
        bind<Application>() with instance(application)
        bind<Database>() with instance(db)
        kodeinMapper(this, application)
    }
    //路由注册
    routing {
        for (binding in di.container.tree.bindings) {
            val clazz = binding.key.type.jvmType as? Class<*>? ?: continue
            if (BaseController::class.java.isAssignableFrom(clazz)) {
                val instance by di.Instance(binding.key.type)
                (instance as BaseController).apply { registerRoutes() }
            }
        }
    }
}

fun Application.configureDB(): Database {
    val config = this.environment.config.config("dataSource")
    val dataSource = DruidDataSource().apply {
        url = config.property("url").getString()
        driverClassName = config.property("drivelClassName").getString()
        username = config.property("username").getString()
        password = config.property("password").getString()
        initialSize = config.property("initialSize").getString().toInt()
        maxActive = config.property("maxActive").getString().toInt()
        minIdle = config.property("minIdle").getString().toInt()
    }

    return Database.connect(dataSource = dataSource)
}

abstract class BaseController : DIAware {
    val application: Application by instance()
    abstract fun Route.registerRoutes()
}

inline fun <reified T : Any> DI.MainBuilder.bindSingleton(crossinline callback: (DI) -> T) {
    bind<T>() with singleton { callback(this@singleton.di) }
}