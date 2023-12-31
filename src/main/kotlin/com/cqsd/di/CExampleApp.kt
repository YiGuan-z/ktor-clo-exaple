package com.cqsd.di

import com.cqsd.plugins.*
import com.cqsd.route.BaseController
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.routing.*
import org.kodein.di.*
import org.kodein.type.jvmType
import org.ktorm.database.Database
import org.slf4j.Logger

/**
 * @author caseycheng
 * @date 2023/7/8 19:25
 * @doc
 */
object App {
    lateinit var diContent: DI
}

fun Application.exampleApp(kodeinMapper: DI.MainBuilder.(Application) -> Unit = {}) {
    val application = this
    // logger
    val log = application.log
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

    App.diContent = DI {
        bind<Application>() with instance(application)
        bind<Database>() with instance(db)
        kodeinMapper(this, application)
    }

    val controllers: List<BaseController> = App.diContent.collect(mutableListOf())

    routing {
        controllers.forEach { controller ->
            val name = controller::class.qualifiedName
            controller.apply {
                log.info("{}的路由已加载", name)
                registerRoutes()
            }
        }
    }

    application.configureStatusPage {
        controllers.forEach { controller -> controller.apply { registerExceptionHandle() } }
    }
}

/**
 * used to Shortcuts
 */
inline fun <reified T : Any> DI.Builder.bindSingleton(crossinline callback: (DI) -> T) {
    bind<T>() with singleton { callback(this@singleton.di) }
}

/**
 * collect of objects in the container
 */
inline fun <reified Type, Coll : MutableCollection<Type>> DI.collect(collection: Coll): Coll {
    val log by this.instance<Logger>()
    for (binding in this.container.tree.bindings) {
        val clazz = binding.key.type.jvmType as? Class<*>? ?: continue
        if (Type::class.java.isAssignableFrom(clazz)) {
            val instance by this.Instance(binding.key.type)
            collection.add(instance as Type)
        }
    }
    return collection
}