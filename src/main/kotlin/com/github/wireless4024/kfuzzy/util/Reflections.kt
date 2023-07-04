package com.github.wireless4024.kfuzzy.util

import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.KTypeParameter

object Reflections {
    /**
     * Retrieves the arguments of a given type, along with their corresponding names,
     * from the provided parameters.
     *
     * @param typ The type to retrieve the arguments from.
     * @param klass The class of the type to retrieve the arguments from.
     * @param generics The mapping of generic parameter names to their actual types.
     * @return A map containing the names and types of the arguments.
     */
    fun getArguments(typ: KType, klass: KClass<*>, generics: Map<String, KType>): Map<String, KType> {
        val args = typ.arguments
        val map = mutableMapOf<String, KType>()
        for ((i, t) in klass.typeParameters.withIndex()) {
            val type = (if (i >= args.size) generics[t.name] else args[i].type) ?: continue
            map[t.name] = if (type.classifier is KTypeParameter && generics.containsKey(t.name)) {
                generics[t.name]!!
            } else {
                type
            }
        }
        return map

    }
}
