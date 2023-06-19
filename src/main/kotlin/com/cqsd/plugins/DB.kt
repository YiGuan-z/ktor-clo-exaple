package com.cqsd.plugins

import com.alibaba.druid.pool.DruidDataSource
import com.cqsd.module.UserDefinition
import com.cqsd.plugins.def.KtormPlugin
import com.cqsd.plugins.def.buildDataSource
import io.ktor.server.application.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.ktorm.database.Database
import org.ktorm.entity.sequenceOf

/**
 *
 * @author caseycheng
 * @date 2023/6/18-21:13
 **/
private const val rootPath = "dataSource"
fun Application.configureKtorm() {
    val fastStart = environment.config.propertyOrNull("$rootPath.fastStartServer")?.getString()?.toBoolean() ?: false
    if (fastStart) {
        launch {
            withContext(Dispatchers.IO) {
                installKtormPlugin()
            }
        }
    } else {
        installKtormPlugin()
    }
}

private fun Application.installKtormPlugin() {
    install(KtormPlugin) {
        val config = environment.config
        buildDataSource {
            DruidDataSource().apply {
                driverClassName = config.property("$rootPath.drivelClassName").getString()
                url = config.property("$rootPath.url").getString()
                username = config.property("$rootPath.username").getString()
                password = config.property("$rootPath.password").getString()
                //初始链接数
                initialSize = config.propertyOrNull("$rootPath.initialSize")?.getString()?.toInt() ?: 5
                //最大链接数量
                maxActive = config.propertyOrNull("$rootPath.maxActive")?.getString()?.toInt() ?: 100
                //最小链接数量
                minIdle = config.propertyOrNull("$rootPath.minIdle")?.getString()?.toInt() ?: 5
            }
        }
    }
}

internal val Database.user get() = this.sequenceOf(UserDefinition)
