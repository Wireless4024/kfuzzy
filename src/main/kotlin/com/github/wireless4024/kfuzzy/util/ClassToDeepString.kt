package com.github.wireless4024.kfuzzy.util

import com.github.wireless4024.kfuzzy.util.DeepToString.Companion.toString

class ClassToDeepString(private val level: Int) {
    val stringBuilder = StringBuilder()
    private val parentIndent = "  ".repeat(level)
    private val indent = "  ".repeat(level + 1)

    fun end() = this

    fun name(classname: String): ClassToDeepString {
        stringBuilder.append(classname).append("(\n")
        return this
    }

    fun field(name: String, value: Any?): ClassToDeepString {
        if (value is DeepToString) return field(name, value)
        stringBuilder.append(indent)
        stringBuilder.append(name).append("=")
        stringBuilder.append(value)
        stringBuilder.append("\n")
        return this
    }

    fun field(name: String, value: DeepToString?): ClassToDeepString {
        stringBuilder.append(indent)
        stringBuilder.append(name).append("=")
        stringBuilder.append(value?.toString(level + 1))
        stringBuilder.append("\n")
        return this
    }

    fun <T> field(name: String, value: Iterable<T?>): ClassToDeepString {
        stringBuilder.append(indent)
        stringBuilder.append(name).append("=")
        stringBuilder.append(value.toString(level + 1))
        stringBuilder.append("\n")
        return this
    }
}
