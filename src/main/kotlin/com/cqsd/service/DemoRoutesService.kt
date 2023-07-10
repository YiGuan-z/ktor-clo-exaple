package com.cqsd.service

import com.cqsd.di.App
import org.kodein.di.DI
import org.kodein.di.DIAware
import java.util.concurrent.ConcurrentHashMap

/**
 *
 * @author caseycheng
 * @date 2023/7/10-14:39
 * @doc
 **/
interface DemoRoutesService : DIAware,Service {
    fun nameIncr(name: String): Int

    companion object Default : DemoRoutesService {
        private val pool: MutableMap<String, Int> = ConcurrentHashMap()

        override fun nameIncr(name: String): Int {
            var counter = pool.getOrPut(name) { 0 }
            counter += 1
            pool[name] = counter
            return counter
        }

        /**
         * A DI Aware class must be within reach of a [DI] object.
         */
        override val di: DI
            get() = App.diContent
    }
}
