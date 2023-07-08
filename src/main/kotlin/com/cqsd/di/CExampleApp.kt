package com.cqsd.di

import com.alibaba.druid.pool.DruidDataSource
import com.cqsd.plugins.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.resources.*
import io.ktor.server.routing.*
import org.kodein.di.*
import org.kodein.type.jvmType
import org.ktorm.database.Database
import org.ktorm.support.mysql.MySqlDialect

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

private object DataSourceConfigKey {
    const val path = "dataSource"
    const val url = "url"
    const val drivelClassName = "drivelClassName"
    const val username = "username"
    const val password = "password"
    const val initialSize = "initialSize"
    const val maxActive = "maxActive"
    const val minIdle = "minIdle"
}

fun Application.configureDB(): Database {
    val config = this.environment.config.config(DataSourceConfigKey.path)
    val dataSource = DruidDataSource().apply {
        url = config.property(DataSourceConfigKey.url).getString()
        driverClassName = config.property(DataSourceConfigKey.drivelClassName).getString()
        username = config.property(DataSourceConfigKey.username).getString()
        password = config.property(DataSourceConfigKey.password).getString()
        initialSize = config.propertyOrNull(DataSourceConfigKey.initialSize)?.getString()?.toInt() ?: 3
        maxActive = config.propertyOrNull(DataSourceConfigKey.maxActive)?.getString()?.toInt() ?: 50
        minIdle = config.propertyOrNull(DataSourceConfigKey.minIdle)?.getString()?.toInt() ?: 5
    }
    return Database.connect(dataSource = dataSource, dialect = MySqlDialect())
}

abstract class BaseController : DIAware {
    val application: Application by instance()
    abstract fun Route.registerRoutes()
    open fun StatusPagesConfig.registerExceptionHandle() {}

}

abstract class DBController : BaseController() {
    val db: Database by instance()
}


inline fun <reified T : Any> DI.MainBuilder.bindSingleton(crossinline callback: (DI) -> T) {
    bind<T>() with singleton { callback(this@singleton.di) }
}