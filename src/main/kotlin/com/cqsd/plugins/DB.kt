package com.cqsd.plugins

import com.cqsd.module.UserDefinition
import com.cqsd.plugins.adaptor.KtormConfig
import com.cqsd.plugins.adaptor.KtormPlugin
import io.ktor.server.application.*
import org.ktorm.database.Database
import org.ktorm.entity.sequenceOf

/**
 *
 * @author caseycheng
 * @date 2023/6/18-21:13
 **/
private const val rootPath = "dataSource"
fun Application.configureKtorm() {
    installKtormPlugin()
}

private fun Application.installKtormPlugin() {
    install(KtormPlugin) {
        val config = environment.config
        //使用多数据源
        manyDataSource = KtormConfig.UseDataSource.Many
        //单数据源快速启动
        fastStartServer = false
//         configureDataSource {
//            DruidDataSource().apply {
//                driverClassName = config.property("$rootPath.drivelClassName").getString()
//                url = config.property("$rootPath.url").getString()
//                username = config.property("$rootPath.username").getString()
//                password = config.property("$rootPath.password").getString()
//                //初始链接数
//                initialSize = config.propertyOrNull("$rootPath.initialSize")?.getString()?.toInt() ?: 5
//                //最大链接数量
//                maxActive = config.propertyOrNull("$rootPath.maxActive")?.getString()?.toInt() ?: 100
//                //最小链接数量
//                minIdle = config.propertyOrNull("$rootPath.minIdle")?.getString()?.toInt() ?: 5
//            }
//        }
    }
}

internal val Database.user get() = this.sequenceOf(UserDefinition)
