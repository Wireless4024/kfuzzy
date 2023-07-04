package com.github.wireless4024.kfuzzy.reflection

import com.github.wireless4024.kfuzzy.util.DeepToString
import com.github.wireless4024.kfuzzy.util.DeepToString.Companion.toString
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.cast
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.full.superclasses

/**
 * Represents a class with runtime information and functionality to convert between objects and data.
 *
 * @param T the type of the class.
 * @property inner the KClass representing the class.
 * @property fields the list of RFields associated with the class.
 * @property annotations the list of annotations associated with the class.
 * @property generics the map of generic types associated with the class.
 * @property isFlat indicates whether the class is flat or not (mark as do not recursively scan).
 */
class RClass<T : Any>(
    val inner: KClass<T>,
    val fields: List<RField>,
    val annotations: List<Annotation>,
    val generics: Map<String, RClass<*>>,
    val isFlat: Boolean,
    val nullable: Boolean,
) : DeepToString {
    val enumValues get(): Array<out Any>? = inner.java.enumConstants

    fun toObject(raw: Any?): T? {
        raw ?: return null

        return if (raw is Map<*, *>) {
            val ctor = if (inner.primaryConstructor?.parameters?.isEmpty() != false)
                inner.superclasses.first().primaryConstructor!!
            else
                inner.primaryConstructor!!
            val args = mutableMapOf<KParameter, Any?>()
            val parameterMap = ctor.parameters.associateByTo(mutableMapOf()) { it.name!! }
            val fieldTable = fields.associateBy { it.name }
            for ((key, value) in raw) {
                val param = parameterMap.remove(key)!!
                val field = fieldTable[key]!!

                args[param] = when (value) {
                    is Long -> castNumber(value, field.type)
                    is Map<*, *> -> {
                        field.type.toObject(value)
                    }

                    is String -> {
                        val enumValues = field.type.enumValues
                        if (enumValues != null) {
                            enumValues.find { it.toString() == value }
                        } else {
                            value
                        }
                    }

                    else -> value
                }
            }

            for ((key, value) in parameterMap) {
                if (value.type.isMarkedNullable && !value.isOptional) {
                    args[value] = null
                }
            }

            @Suppress("UNCHECKED_CAST")
            return ctor.callBy(args) as? T
        } else {
            inner.cast(raw)
        }
    }

    private fun castNumber(number: Long, klass: RClass<*>): Number {
        return when (klass.inner) {
            Byte::class -> number.toByte()
            Short::class -> number.toShort()
            Int::class -> number.toInt()
            Float::class -> number.toFloat()
            Double::class -> number.toDouble()
            else -> number
        }
    }

    override fun toString(): String {
        return toString(0)
    }

    fun withNullable(nullable: Boolean): RClass<T> {
        return RClass(inner, fields, annotations, generics, isFlat, nullable)
    }

    override fun toString(deep: Int): String {
        val parentIndent = "  ".repeat(deep)
        val indent = "  ".repeat(deep + 1)
        return "RClass(\n" +
                "${indent}inner=$inner,\n" +
                "${indent}fields=${fields.toString(deep + 1)},\n" +
                "${indent}annotations=${annotations},\n" +
                "${indent}generics=${generics.toString(deep + 1)},\n" +
                "${indent}nullable=${nullable}\n" +
                "${parentIndent})"
    }

    companion object {
        /**
         * Converts a given Kotlin class [kClass] to its corresponding structure using reflection.
         *
         * @param kClass The Kotlin class to convert.
         * @return The converted structure of the class.
         */
        fun <T : Any> fromClass(kClass: KClass<T>) = ReflectionHelper.scanStructure(kClass, listOf(), mapOf())
    }
}
