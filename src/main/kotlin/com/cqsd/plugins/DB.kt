package com.cqsd.plugins

import com.alibaba.druid.pool.DruidDataSource
import com.cqsd.module.UserDefinition
import com.cqsd.plugins.ktorm.adaptor.*
import io.ktor.server.application.*
import org.ktorm.database.Database
import org.ktorm.dsl.from

/**
 *
 * @author caseycheng
 * @date 2023/6/18-21:13
 **/
fun Application.configureKtorm() {
    installKtormPlugin()
}

private fun Application.installKtormPlugin() {
    install(KtormConfigPlugin) {
        //使用多数据源
//        useManyDataSource()
        useSingleDataSource()
        //单数据源快速启动
        configureBuildDataSourceCollection { conf ->
            DruidDataSource()
                .apply {
                    url = conf[ConfigField.Url.getKey()].toString()
                    driverClassName = conf[ConfigField.DrivelClassName.getKey()].toString()
                    username = conf[ConfigField.Username.getKey()].toString()
                    password = conf[ConfigField.Password.getKey()].toString()
                    conf[ConfigField.InitialSize.getKey()]?.let { initialSize = it.toString().toInt() }
                    conf[ConfigField.MaxActive.getKey()]?.let { maxActive = it.toString().toInt() }
                    conf[ConfigField.MinIdle.getKey()]?.let { minIdle = it.toString().toInt() }
                }
        }
    }
}

internal val Database.users get() = this.from(UserDefinition)
