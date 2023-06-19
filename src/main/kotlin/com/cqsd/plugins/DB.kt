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
fun Application.configureKtorm() {
    val dataSource = DruidDataSource().apply {
        //需要进行配置化
        driverClassName = "com.mysql.cj.jdbc.Driver"
        url = "jdbc:mysql://49.232.150.194:3306/mybatis_plus"
        username = "root"
        password = "5201314zFy@"
    }
    ::database.set(
        Database.connect(
           dataSource = dataSource
        )
    )
}

internal val Database.user get() = this.sequenceOf(UserDefinition)
lateinit var database: Database