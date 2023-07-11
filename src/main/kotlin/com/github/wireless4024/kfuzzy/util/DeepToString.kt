package com.github.wireless4024.kfuzzy.util

/**
 * Interface for converting objects to deep string representation.
 */
interface DeepToString {
    fun toString(deep: Int): StringBuilder = StringBuilder(javaClass.simpleName)

    companion object {
        @JvmStatic
        fun Iterable<Any?>.toString(deep: Int): StringBuilder {
            val sb = StringBuilder()
            val parentIndent = "  ".repeat(deep)
            val indent = "  ".repeat(deep + 1)
            sb.append("[\n")

            var len = 0
            for (any in this) {
                ++len
                sb.append(indent)
                if (any is DeepToString) {
                    sb.append(any.toString(deep + 1))
                } else {
                    sb.append(any)
                }
                sb.append(",\n")
            }
            if (len != 0) {
                sb.deleteCharAt(sb.length - 2)
            } else {
                sb.delete(sb.length - 1, sb.length)
                return sb.append("]")
            }

            sb.append(parentIndent)
            sb.append("]")
            return sb
        }

        @JvmStatic
        fun Iterable<Any>.toDeepString(deep: Int): StringBuilder {
            val sb = StringBuilder()
            val parentIndent = "  ".repeat(deep)
            val indent = "  ".repeat(deep + 1)
            sb.append("[\n")

            var len = 0
            for (any in this) {
                ++len
                sb.append(indent).append(any).append(",\n")
            }
            if (len != 0) {
                sb.deleteCharAt(sb.length - 2)
            } else {
                sb.delete(sb.length - 1, sb.length)
                return sb.append("]")
            }

            sb.append(parentIndent)
            sb.append("]")
            return sb
        }

        @JvmStatic
        fun Map<out Any, Any?>.toString(deep: Int): StringBuilder {
            val sb = StringBuilder()
            val parentIndent = "  ".repeat(deep)
            val indent = "  ".repeat(deep + 1)
            sb.append("{\n")

            var len = 0
            for ((key, value) in this) {
                ++len
                sb.append(indent)
                    .append(key)
                    .append(": ")
                    .apply {
                        if (value is DeepToString)
                            append(value.toString(deep + 1))
                        else
                            append(value)
                    }
                    .append(",\n")
            }
            if (len != 0) {
                sb.deleteCharAt(sb.length - 2)
            } else {
                sb.delete(sb.length - 1, sb.length)
                return sb.append("}")
            }

            sb.append(parentIndent)
            sb.append("}")
            return sb
        }

        @JvmStatic
        fun classToDeepString(level: Int, block: ClassToDeepString.() -> Unit) =
            ClassToDeepString(level).also(block).end().stringBuilder
    }
}
