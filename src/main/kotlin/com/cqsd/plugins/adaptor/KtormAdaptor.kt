package com.cqsd.plugins.adaptor

import com.cqsd.plugins.adaptor.KtormAdaptorException.DataSourceInitializeException.MultipleDataInitializeException
import io.ktor.server.application.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.ktorm.database.Database
import javax.sql.DataSource
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 *
 * @author caseycheng
 * @date 2023/6/19-16:51
 **/
private var db: Database? = null

//单数据源,如果设置了多数据源，这里将会抛出异常
val Application.database: Database by lazy { db!! }

private var dataBaseCollection: MutableMap<String, Database> = hashMapOf()

//多数据源 数据源名字 数据源值
val Application.databaseCollection: Map<String, Database>
    get() {
        return dataBaseCollection
    }

/**
 * TODO 需要添加多数据源，首先编写DSL并在代码中接上多数据源，多数据源块和单数据源块不能共存，只能单独使用。
 *  不需要定义默认值，检测到有配置[DataSource]则使用[DataSource]，没有就检测配置，配置也没有就会出异常
 *  创建出多数据源后，编写一个object并将其挂载到Application中，通过多数据源中的name属性来获取[Database]
 *  使用单数据源还是多数据源将在config中声明，如果有没在config中声明，那么就是单数据源
 */
val KtormPlugin = createApplicationPlugin("KtormPlugin", ::KtormConfig) {
    val map = this.environment?.config?.toMap()
    if (pluginConfig.manyDataSource == KtormConfig.UseDataSource.Many) {
        //多数据源路径
        val readManyDataSourceConfig = readManyDataSourceConfig(map)
        //将解析完毕的配置添加到集合中
        //如果代码中配置了相同的url，则进行去重
        pluginConfig.dataSourceCollection+=readManyDataSourceConfig
        initManyDataSource(readManyDataSourceConfig)
    } else {
        //单数据源路径
        val sourceConfig = readSingleDataSourceConfig(map)
        ///如果读出来没有url的话，就看代码配置有没有，如果也没有就抛出异常
        initDataSource()
    }
}

/**
 * 读取配置文件,根据配置文件解析多数据源
 */
private fun PluginBuilder<KtormConfig>.readManyDataSourceConfig(map: Map<String, Any?>?): Collection<DataSourceConfig> {
    /*
    * password
    * fastStartServer
    * name
    * drivelClassName
    * url
    * username
    * initialSize
    * maxActive
    * minIdle
    * */
    TODO()
}

/**
 * 读取配置文件，根据配置文件解析单数据源
 */
private fun PluginBuilder<KtormConfig>.readSingleDataSourceConfig(map: Map<String, Any?>?): DataSourceConfig {
    /*
    * drivelClassName
    * url
    * username
    * password
    * initialSize
    * maxActive
    * minIdle
    * fastStartServer
    * */
    TODO()
}

/**
 * 根据配置对单数据源进行初始化
 * TODO 解析配置
 */
internal fun PluginBuilder<KtormConfig>.initDataSource() {
    val config = pluginConfig
    if (config.dataSource == null) {
        //如果数据源为空，则使用连接配置
        fun initDataBase() = Database.connect(
            url = config.url!!,
            user = config.username,
            password = config.password,
            driver = config.driverClassName
        )
        //使用协程快速初始化
        if (config.fastStartServer) {
            application.launch(context = Dispatchers.IO) {
                ::db.set(
                    initDataBase()
                )
            }
        } else {
            ::db.set(
                initDataBase()
            )
        }

    } else {
        //如果数据源不空，则直接使用数据源
        fun initDatabase() = Database.connect(
            dataSource = config.dataSource!!
        )
        if (config.fastStartServer) {
            application.launch(context = Dispatchers.IO) {
                ::db.set(
                    initDatabase()
                )
            }
        } else {
            ::db.set(
                initDatabase()
            )
        }
    }
}

/**
 * 对多数据源进行初始化,多数据源使用[KtormConfig.dataSourceCollection]进行初始化
 * 里面的配置只适合单数据源，如果配置了多数据源想返回单数据源的，请在配置最后将[KtormConfig.manyDataSource]设置为false
 * TODO 解析配置
 */
internal fun PluginBuilder<KtormConfig>.initManyDataSource(readManyDataSourceConfig: Collection<DataSourceConfig>) {
    val config = pluginConfig
    if (config.dataSourceCollection.isNotEmpty()) {
        config.dataSourceCollection.forEach { dataSourceConfig ->
            if (dataSourceConfig.fastStartServer) {
                application.launch {
                    withContext(Dispatchers.IO) {
                        val database = Database.connect(
                            dataSource = dataSourceConfig.dataSource
                        )
                        dataBaseCollection += dataSourceConfig.name to database
                    }
                }
            } else {
                val database = Database.connect(
                    dataSource = dataSourceConfig.dataSource
                )
                dataBaseCollection += dataSourceConfig.name to database
            }
        }
    }
    throw MultipleDataInitializeException("Multi-data source initialization failed")
}

class KtormConfig {
    //代码里的数据源配置和配置文件的数据源配置都要进入这里
    internal var dataSourceCollection: MutableList<DataSourceConfig> = mutableListOf()
    var dataSource: DataSource? = null
    var driverClassName: String? = null
    var url: String? = null
    var username: String? = null
    var password: String? = null

    //确定是单数据源还是多数据源
    var manyDataSource: UseDataSource = UseDataSource.Single

    //标识单数据源是否需要快速启动
    var fastStartServer: Boolean = false

    enum class UseDataSource {
        //使用单数据源
        Single,

        //使用多数据源
        Many
    }
}

//对数据源的配置
data class DataSourceConfig(
    val name: String,
    val fastStartServer: Boolean = false,
    val dataSource: DataSource
)

/**
 * configure [DataSource]
 * 配置单个数据源对象
 */
fun KtormConfig.configureDataSource(dataSourceScope: () -> DataSource) {
    val dataSource = dataSourceScope()
    this.dataSource = dataSource
}

/**
 * 配置多个数据源，通过方法进行构建
 */
@OptIn(ExperimentalContracts::class)
fun KtormConfig.configureDataSourceCollection(dataSourceScope: () -> Collection<DataSourceConfig>) {
    contract {
        callsInPlace(dataSourceScope, InvocationKind.EXACTLY_ONCE)
    }
    val dataSource = dataSourceScope()
    this.dataSourceCollection += dataSource
}

/**
 * 配置多个数据源，通过传入的[DataSourceConfig]对象进行配置
 */
fun KtormConfig.configureDataSourceCollection(vararg dataSource: DataSourceConfig) {
    this.dataSourceCollection += dataSource
}
