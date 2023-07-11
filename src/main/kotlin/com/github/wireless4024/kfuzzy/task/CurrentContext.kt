package com.github.wireless4024.kfuzzy.task

import com.github.wireless4024.kfuzzy.exception.VerificationException
import com.github.wireless4024.kfuzzy.util.DeepToString
import com.github.wireless4024.kfuzzy.util.DeepToString.Companion.classToDeepString
import java.util.*
import kotlin.reflect.KClass

class CurrentContext(
    val taskContext: TaskContext,
    var body: Any? = null,
    val arguments: MutableMap<Any, Any?> = mutableMapOf()
) : DeepToString {
    var success = true
        private set
    var error: Throwable? = null
        private set

    fun fail(err: Throwable) {
        success = false
        error = err
    }

    infix fun Any.asState(key: String) {
        taskContext.set(key, this)
    }

    infix fun <T : Any> T.asState(key: KClass<T>) {
        taskContext.set(key, this)
    }

    @Synchronized
    fun <T> getOrDefault(key: String, default: (key: Any) -> T): MutableSet<T> {
        return taskContext.getOrDefault(key, default)
    }

    operator fun String.unaryPlus(): Any {
        return taskContext.get(this)
    }

    operator fun <T : Any> KClass<T>.unaryPlus(): T {
        return taskContext.get(this)
    }

    infix fun Any?.shouldBe(other: Any?) {
        verify(Objects.equals(this, other), "Value was '%s' but expected '%s'", this, other)
    }

    fun verify(bool: Boolean, message: String, vararg format: Any?) {
        if (!bool) {
            throw VerificationException(message.format(*format))
        }
    }

    override fun toString(): String {
        return "CurrentContext(body=$body, arguments=$arguments, success=$success, error=${error?.message})"
    }

    override fun toString(deep: Int) = classToDeepString(deep) {
        name("CurrentContext")
            .field("body", body)
            .field("arguments", arguments)
            .field("success", success)
            .field("error", error)
    }
}
