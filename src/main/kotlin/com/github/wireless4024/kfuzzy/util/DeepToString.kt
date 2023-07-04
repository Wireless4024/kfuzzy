package com.github.wireless4024.kfuzzy.util

/**
 * Interface for converting objects to deep string representation.
 */
interface DeepToString {
    fun toString(deep: Int): String = javaClass.simpleName

    companion object {
        @JvmStatic
        fun Iterable<DeepToString>.toString(deep: Int): StringBuilder {
            val sb = StringBuilder()
            val parentIndent = "  ".repeat(deep)
            val indent = "  ".repeat(deep + 1)
            sb.append("[\n")

            var len = 0
            for (any in this) {
                ++len
                sb.append(indent).append(any.toString(deep + 1)).append(",\n")
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
        fun Map<out Any, DeepToString>.toString(deep: Int): StringBuilder {
            val sb = StringBuilder()
            val parentIndent = "  ".repeat(deep)
            val indent = "  ".repeat(deep + 1)
            sb.append("{\n")

            var len = 0
            for ((key, value) in this) {
                ++len
                sb.append(indent).append(key).append(": ").append(value.toString(deep + 1)).append(",\n")
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
    }
}
