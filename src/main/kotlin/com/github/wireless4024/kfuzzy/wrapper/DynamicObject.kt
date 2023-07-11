package com.github.wireless4024.kfuzzy.wrapper

import com.github.wireless4024.kfuzzy.reflection.RClass
import com.github.wireless4024.kfuzzy.util.DeepToString
import com.github.wireless4024.kfuzzy.util.DeepToString.Companion.toString
import kotlin.reflect.KClass
import kotlin.reflect.cast

class DynamicObject(val inner: Map<String, Any?>) : DeepToString {
    fun getRaw(key: String): Any? {
        var parent: Any? = inner
        for (path in key.splitToSequence('.')) {
            parent = when (parent) {
                is Map<*, *> -> {
                    parent[path]
                }

                is DynamicObject -> {
                    parent.getRaw(path)
                }

                is List<*> -> {
                    parent.getOrNull(path.toIntOrNull() ?: return null)
                }

                else -> {
                    return null
                }
            }
        }
        return parent
    }

    inline operator fun <reified T : Any> get(key: String): T? = getRaw(key)?.run(T::class::cast)

    fun getObjectOrNull(key: String) = (get(key) as? Map<String, Any?>)?.let(::DynamicObject)

    fun getObject(key: String) = getObjectOrNull(key)!!

    @Suppress("UNCHECKED_CAST")
    fun <T> getListOrNull(key: String): List<T>? = get(key) as? List<T>

    fun <T> getList(key: String): List<T> = getListOrNull(key)!!

    fun <T : Any> castIntoKClass(kClass: KClass<T>): T? {
        return RClass.fromClass(kClass).toObject(inner)
    }

    inline fun <reified T : Any> castInto(): T? = castIntoKClass(T::class)

    override fun toString(): String {
        return inner.toString()
    }

    override fun toString(deep: Int): StringBuilder {
        return inner.toString(deep)
    }
}
