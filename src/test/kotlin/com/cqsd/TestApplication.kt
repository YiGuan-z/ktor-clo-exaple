package com.cqsd

import com.cqsd.plugins.configureHTTP
import com.cqsd.plugins.configureKtorm
import com.cqsd.plugins.configureSecurity
import com.cqsd.plugins.configureSerialization
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.testing.*
import org.kodein.di.DI
import org.kodein.di.Instance
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.type.jvmType
import org.ktorm.database.Database

/**
 *
 * @author caseycheng
 * @date 2023/7/9-23:41
 * @doc
 **/
internal lateinit var application: Application
internal lateinit var database: Database
fun ApplicationTestBuilder.testApplication(
    kodeinMapper: DI.MainBuilder.(ApplicationTestBuilder) -> Unit = {}
) {
    this.application {
        application = this
        configureHTTP()
        database = configureKtorm()
        //resource
        application.install(Resources)
        // http
        application.configureHTTP()
        //security
        application.configureSecurity()
        //serialization
        application.configureSerialization()
    }
    val di = DI {
        bind<Application>() with instance(application)
        bind<Database>() with instance(database)
    }
    for (binding in di.container.tree.bindings) {

    }

}

inline fun <reified Type, O : MutableCollection<Type>> DI.collect(collection: O): O {
    for (binding in this.container.tree.bindings) {
        val clazz = binding.key.type.jvmType as? Class<*>? ?: continue
        if (clazz.isAssignableFrom(Type::class.java)) {
            val instance by di.Instance(binding.key.type)
            collection.add(instance as Type)
        }
    }
    return collection
}