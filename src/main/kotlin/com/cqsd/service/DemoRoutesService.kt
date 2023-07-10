package com.cqsd.service

import org.kodein.di.DIAware

/**
 *
 * @author caseycheng
 * @date 2023/7/10-14:39
 * @doc
 **/
interface DemoRoutesService : DIAware {
    fun nameIncr(name:String): Int
}
