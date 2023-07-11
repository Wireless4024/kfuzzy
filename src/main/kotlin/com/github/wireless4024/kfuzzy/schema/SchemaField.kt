package com.github.wireless4024.kfuzzy.schema

import com.github.wireless4024.kfuzzy.faker.IFaker
import com.github.wireless4024.kfuzzy.field.FieldKind
import com.github.wireless4024.kfuzzy.generator.Generator
import com.github.wireless4024.kfuzzy.util.DeepToString
import com.github.wireless4024.kfuzzy.util.DeepToString.Companion.classToDeepString

/**
 * Represents a schema field.
 *
 * @param kind The [FieldKind] of the schema field.
 * @param generators The list of generators associated with the schema field.
 */
data class SchemaField(val kind: FieldKind, val generators: List<Generator>) : DeepToString {
    fun successCase(faker: IFaker): Any? {
        return generators.random().successCase(kind, faker)
    }

    fun possibleSuccessCase(faker: IFaker): List<Any?> {
        return generators.flatMap { it.possibleSuccessCase(kind, faker) }
    }

    fun possibleFailCase(faker: IFaker): List<Any?> {
        return generators.flatMap { it.possibleFailCase(kind, faker) }
    }

    override fun toString(deep: Int) = classToDeepString(deep) {
        name("SchemaField")
            .field("kind", kind)
            .field("generators", generators)
    }

    override fun toString(): String {
        return "SchemaField(kind=$kind, generators=$generators)"
    }
}
