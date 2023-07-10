package com.cqsd.route

import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.routing.*
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.instance
import org.ktorm.database.Database

/**
 * @author caseycheng
 * @date 2023/7/9 11:58
 * @doc
 */
abstract class BaseController : DIAware {
    val application: Application by instance()

    /**
     * 路由注册
     */
    abstract fun Route.registerRoutes()

    /**
     * 全局异常处理器注册
     */
    open fun StatusPagesConfig.registerExceptionHandle() {}

    /**
     * 模块构建
     */
    open fun DI.Builder.registerModule(){}
}

abstract class DBController : BaseController() {
    val db: Database by instance()
}