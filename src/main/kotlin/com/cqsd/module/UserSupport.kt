package com.cqsd.module

import io.ktor.server.auth.*
import kotlinx.serialization.Serializable
import org.ktorm.dsl.QueryRowSet
import org.ktorm.schema.BaseTable
import org.ktorm.schema.int
import org.ktorm.schema.long
import org.ktorm.schema.varchar

/**
 *
 * @author caseycheng
 * @date 2023/6/18-20:43
 **/
//class User constructor(var id: Long, var name: String?, var password: String)
@Serializable
data class User(
    val id: Long,
    var name: String,
    var age: Int,
    var email: String
)

object UserDefinition : BaseTable<User>("user") {
    val id = long("id").primaryKey()
    val name = varchar("name")
    val age = int("age")
    val email = varchar("email")

    /**
     * Create an entity object from the specific row of query results.
     *
     * This function is called by [createEntity]. Subclasses should override it and implement the actual logic of
     * retrieving an entity object from the query results.
     */
    override fun doCreateEntity(row: QueryRowSet, withReferences: Boolean): User {
        return User(
            id = row[id] ?: 0L,
            name = row[name] ?: "",
            age = row[age] ?: 0,
            email = row[email] ?: ""
        )
    }
}

data class UserPrincipal(val name: String?, val userId: Long) : Principal
//class User {
//    var id: Long = 0
//    var name: String? = null
//    var age: Int = 0
//    var email: String? = null
//
//    constructor(id: Long, name: String?, age: Int, email: String?) {
//        this.id = id
//        this.name = name
//        this.age = age
//        this.email = email
//    }
//}