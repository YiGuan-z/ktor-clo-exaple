package com.cqsd.service

import com.cqsd.dao.User
import org.kodein.di.DIAware

/**
 *
 * @author caseycheng
 * @date 2023/7/10-15:15
 * @doc
 **/
interface LoginRoutesService : DIAware {
    fun createJWT(user: User, millis: Long = 60000): JwtToken
}
typealias JwtToken=String