package com.cqsd.plugins

import com.alibaba.druid.pool.DruidDataSource
import com.cqsd.module.UserDefinition
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
    val dataSource = DruidDataSource().apply {
        val config = environment.config
        //需要进行配置化
        driverClassName = config.property("$rootPath.drivelClassName").getString()
        url = config.property("$rootPath.url").getString()
        username = config.property("$rootPath.username").getString()
        password = config.property("$rootPath.password").getString()
        //初始链接数
        initialSize = config.propertyOrNull("$rootPath.initialSize")?.getString()?.toInt() ?: 5
        //最大链接数量
        maxActive = config.propertyOrNull("$rootPath.maxActive")?.getString()?.toInt() ?: 50
        //最小链接数量
        minIdle = config.propertyOrNull("$rootPath.minIdle")?.getString()?.toInt() ?: 5
    }
    ::database.set(
        Database.connect(
            dataSource = dataSource
        )
    )
}

lateinit var database: Database
internal val Database.user get() = this.sequenceOf(UserDefinition)
