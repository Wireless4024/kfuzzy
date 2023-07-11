package com.github.wireless4024.kfuzzy.schema

import com.github.wireless4024.kfuzzy.faker.IFaker
import com.github.wireless4024.kfuzzy.reflection.RClass
import com.github.wireless4024.kfuzzy.util.DeepToString
import com.github.wireless4024.kfuzzy.util.DeepToString.Companion.classToDeepString
import com.github.wireless4024.kfuzzy.util.DeepToString.Companion.toString
import com.github.wireless4024.kfuzzy.util.Expander
import com.github.wireless4024.kfuzzy.wrapper.DynamicObject
import kotlin.reflect.KClass

/**
 * Represents a schema for mapping fields to their respective schema fields.
 * Provides methods for generating success and failure cases based on the defined schema.
 *
 * @property fields The map of field names to their corresponding schema fields.
 * @constructor Creates a Schema instance with the given map of fields.
 *
 * @param fields The map of field names to their corresponding schema fields.
 */
data class Schema(val fields: Map<String, SchemaField>) : DeepToString {
    constructor(rClass: RClass<*>) : this(SchemaExtractor.fromRClass(rClass))
    constructor(kClass: KClass<*>) : this(RClass.fromClass(kClass))

    fun successCase(faker: IFaker): DynamicObject {
        val obj = mutableMapOf<String, Any>()
        for ((k, v) in fields) {
            obj[k] = v.successCase(faker) ?: continue
        }
        return DynamicObject(obj)
    }

    fun possibleSuccessCase(faker: IFaker): Sequence<DynamicObject> {
        val map = fields.entries
            .asSequence()
            .map { (k, v) -> k to (v.possibleSuccessCase(faker)) }
            .associate { it }
        return Expander(map).asSequence().map(::DynamicObject)
    }

    fun possibleFailCase(faker: IFaker): Sequence<DynamicObject> {
        val map = fields.entries
            .map { (k, v) -> k to v.possibleFailCase(faker) }
            .associate { it }
        return Expander(map).asSequence().map(::DynamicObject)
    }


    override fun toString(deep: Int) = classToDeepString(deep){
        name("Schema")
            .field("fields",fields)
    }

    override fun toString(): String {
        return "Schema(fields=$fields)"
    }
}
