package com.cqsd.service.impl

import com.cqsd.service.DemoRoutesService
import org.kodein.di.DI
import java.util.concurrent.ConcurrentHashMap

/**
 *
 * @author caseycheng
 * @date 2023/7/10-14:41
 * @doc
 **/
class DemoRoutesServiceImpl(override val di: DI) : DemoRoutesService {
    private val pool: MutableMap<String, Int> = ConcurrentHashMap()

    override fun nameIncr(name: String): Int {
        var counter = pool.getOrPut(name) { 0 }
        counter += 1
        pool[name] = counter
        return counter
    }
}