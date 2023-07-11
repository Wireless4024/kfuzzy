package com.github.wireless4024.kfuzzy.reflection

import com.github.wireless4024.kfuzzy.util.DeepToString
import com.github.wireless4024.kfuzzy.util.DeepToString.Companion.classToDeepString
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
    override fun toString(deep: Int) = classToDeepString(deep) {
        name("RField")
            .field("inner", inner)
            .field("name", name)
            .field("type", type)
            .field("nullable", nullable)
            .field("optional", optional)
            .field("annotations", annotations)
    }

    override fun toString(): String {
        return "RField(inner=$inner, name='$name', type=$type, nullable=$nullable, optional=$optional, annotations=$annotations)"
    }
}
