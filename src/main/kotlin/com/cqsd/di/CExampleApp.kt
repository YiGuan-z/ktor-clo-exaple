package com.cqsd.di

import com.cqsd.plugins.*
import com.cqsd.route.BaseController
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
internal lateinit var di: DI
fun Application.exampleApp(kodeinMapper: DI.MainBuilder.(Application) -> Unit = {}) {
    val application = this
    // logging
    application.configureLogging()
    //db
    val db = application.configureKtorm()
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
    val controllers = di.container.tree.bindings
        .filter {
            val clazz = it.key.type.jvmType as? Class<*>?
            return@filter clazz != null && BaseController::class.java.isAssignableFrom(clazz)
        }
        .map { val controller by di.Instance(it.key.type); controller as BaseController }

    routing {
        controllers.forEach { controller -> controller.apply { registerRoutes() } }
    }

    application.configureStatusPage {
        controllers.forEach { controller -> controller.apply { registerExceptionHandle() } }
    }
}


inline fun <reified T : Any> DI.MainBuilder.bindSingleton(crossinline callback: (DI) -> T) {
    bind<T>() with singleton { callback(this@singleton.di) }
}