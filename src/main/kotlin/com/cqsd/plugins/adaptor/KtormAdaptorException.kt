package com.cqsd.plugins.adaptor

/**
 *
 * @author caseycheng
 * @date 2023/6/20-10:55
 **/
sealed class KtormAdaptorException(string: String, cause: Throwable? = null) : RuntimeException(string, cause){
    open class DataSourceInitializeException(string: String, cause: Throwable? = null) : KtormAdaptorException(string, cause){
        class SingleDataInitializeException(string: String, cause: Throwable? = null) : DataSourceInitializeException(string, cause)
        class MultipleDataInitializeException(string: String, cause: Throwable? = null) : DataSourceInitializeException(string, cause)
    }
}