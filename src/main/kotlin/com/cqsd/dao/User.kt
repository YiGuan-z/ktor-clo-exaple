package com.cqsd.dao

import kotlinx.serialization.Serializable
import org.ktorm.database.Database
import org.ktorm.dsl.QueryRowSet
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.BaseTable
import org.ktorm.schema.int
import org.ktorm.schema.long
import org.ktorm.schema.varchar

/**
 *
 * @author caseycheng
 * @date 2023/7/9-22:11
 * @doc
 **/
internal val Database.user get() = this.sequenceOf(UserDefinition)
//数据模型
@Serializable
data class User(
    val id: Long,
    var name: String,
    var age: Int,
    var email: String
)

//数据库声明
object UserDefinition : BaseTable<User>("user") {
    val id = long("id").primaryKey()
    val name = varchar("name")
    val age = int("age")
    val email = varchar("email")

    override fun doCreateEntity(row: QueryRowSet, withReferences: Boolean): User {
        return User(
            id = row[id] ?: -1,
            name = row[name] ?: "",
            age = row[age] ?: 0,
            email = row[email] ?: ""
        )
    }
}