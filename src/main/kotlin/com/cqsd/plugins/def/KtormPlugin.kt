package com.cqsd.plugins.def

import io.ktor.server.application.*
import org.ktorm.database.Database
import javax.sql.DataSource

/**
 *
 * @author caseycheng
 * @date 2023/6/19-16:51
 **/
private var db: Database? = null
val Application.database: Database
    get() {
        return if (db == null) {
            throw NullPointerException("KtormPlugin not installed")
        } else {
            db!!
        }
    }

val KtormPlugin = createApplicationPlugin("KtormPlugin", ::KtormConfig) {
    if (pluginConfig.dataSource == null) {
        //如果数据源为空，则使用连接配置
        ::db.set(
            Database.connect(
                url = pluginConfig.url,
                user = pluginConfig.username,
                password = pluginConfig.password,
                driver = pluginConfig.driverClassName
            )
        )
    } else {
        //如果数据源不空，则直接使用数据源
        ::db.set(
            Database.connect(
                dataSource = pluginConfig.dataSource!!
            )
        )
    }
}

class KtormConfig {
    var dataSource: DataSource? = null
    var driverClassName: String = "com.mysql.cj.jdbc.Driver"
    var url: String = "jdbc:mysql://localhost:3306"
    var username: String = "root"
    var password: String = ""
}

fun KtormConfig.buildDataSource(dataSourceScope: () -> DataSource) {
    val dataSource = dataSourceScope()
    this.dataSource = dataSource
}

