package com.github.wireless4024.kfuzzy.reflection

import com.github.wireless4024.kfuzzy.util.DeepToString
import kotlin.reflect.KProperty1


/**
 * A class representing a field.
 *
 * @property inner The inner property of the field.
 * @property name The name of the field.
 * @property type The type of the field.
 * @property nullable Whether the field is nullable or not.
 * @property optional Whether the field is optional or not.
 * @property annotations The list of annotations applied to the field.
 */
class RField(
    val inner: KProperty1<*, *>,
    val name: String,
    val type: RClass<*>,
    val nullable: Boolean,
    val optional: Boolean,
    val annotations: List<Annotation>
) : DeepToString {
    override fun toString(): String {
        return toString(0)
    }

    override fun toString(deep: Int): String {
        val parentIndent = "  ".repeat(deep)
        val indent = "  ".repeat(deep + 1)
        return "RField(\n" +
                "${indent}inner=$inner,\n" +
                "${indent}name=$name,\n" +
                "${indent}type=${type.toString(deep + 1)},\n" +
                "${indent}nullable=$nullable,\n" +
                "${indent}optional=$optional,\n" +
                "${indent}annotations=$annotations\n" +
                "${parentIndent})"
    }
}
