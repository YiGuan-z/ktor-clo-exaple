package com.cqsd.plugins

import com.alibaba.druid.pool.DruidDataSource
import io.ktor.server.application.*
import org.ktorm.database.Database
import org.ktorm.support.mysql.MySqlDialect

/**
 *
 * @author caseycheng
 * @date 2023/7/9-21:58
 * @doc
 **/
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
fun Application.configureKtorm():Database{
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
