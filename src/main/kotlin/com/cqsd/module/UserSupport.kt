package com.cqsd.module

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
@Serializable
data class UserLoginRequest(val id:Long,val age: Int)
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
            id = row[id] ?: -1,
            name = row[name] ?: "",
            age = row[age] ?: 0,
            email = row[email] ?: ""
        )
    }
}