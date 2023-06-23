package com.cqsd

import kotlin.test.Test

/**
 *
 * @author caseycheng
 * @date 2023/6/23-12:37
 **/
class ByteCodeTest {
    @Test
    fun intAndInteger() {
        val a = 1
        val b: Int = 1 as Int
        val c: Int? = 1
        val d: Integer = 1 as Integer
        val e: Integer? = 1 as Integer
        println(a::class.java)
        println(b::class.java)
        c?.let {
            println(c::class.java)
        }
        println(d::class.java)
        e?.let {
            println(e::class.java)
        }
    }
}
