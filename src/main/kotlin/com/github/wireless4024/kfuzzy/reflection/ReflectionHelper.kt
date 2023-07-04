package com.github.wireless4024.kfuzzy.reflection

import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.KType
import kotlin.reflect.KTypeParameter
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.full.superclasses

object ReflectionHelper {
    /**
     * Scans the structure of a given class and generates a representation of the class.
     *
     * @param klass The class to scan.
     * @param annotations The list of annotations to apply to the class.
     * @param generics The map of generic types to use for parameterizing the class.
     * @return The representation of the scanned class.
     */
    fun <T : Any> scanStructure(
        klass: KClass<T>,
        annotations: List<Annotation>,
        generics: Map<String, RClass<*>>
    ): RClass<T> {
        val fields = mutableListOf<RField>()
        for (property in klass.memberProperties) {
            val optional = klass.primaryConstructor
                ?.parameters
                ?.ifEmpty {
                    klass.superclasses.firstOrNull { it.isAbstract || it.isOpen }
                        ?.primaryConstructor
                        ?.parameters
                }
                ?.find { it.name == property.name }
                ?.isOptional

            fields += scanField(property, optional == true, generics)
        }
        return RClass(klass, fields, annotations, generics, isFlat = false, nullable = false)
    }


    private fun scanField(field: KProperty1<*, *>, optional: Boolean, generics: Map<String, RClass<*>>): RField {
        val name = field.name
        val type = solveType(field.returnType, generics)
        val nullable = field.returnType.isMarkedNullable
        val annotations = field.annotations
        return RField(field, name, type, nullable, optional, annotations)
    }

    /**
     * Determines the resolved type for the given input type and generics.
     *
     * @param type The input type to determine the resolved type for.
     * @param generics The map of generic type parameter names to their resolved types.
     * @return The resolved type, represented as an instance of [RClass].
     */
    private fun solveType(type: KType, generics: Map<String, RClass<*>>): RClass<*> {
        val classifier = type.classifier
        return if (classifier is KClass<*>) {
            when {
                classifier == String::class || classifier.javaPrimitiveType != null ->
                    RClass(classifier, listOf(), type.annotations, mapOf(), true, type.isMarkedNullable)

                classifier.isSubclassOf(Enum::class) -> RClass(
                    classifier,
                    listOf(),
                    listOf(),
                    mapOf(),
                    true,
                    type.isMarkedNullable
                )

                classifier.isSubclassOf(Collection::class) -> {
                    val genericMap = solveGeneric(
                        classifier.typeParameters,
                        type.arguments.mapNotNull { it.type },
                        generics
                    )
                    RClass(classifier, listOf(), type.annotations, genericMap, true, type.isMarkedNullable)
                }

                else -> {
                    scanStructure(
                        classifier,
                        type.annotations,
                        solveGeneric(
                            classifier.typeParameters,
                            type.arguments.mapNotNull { it.type },
                            generics
                        )
                    )
                }
            }
        } else {
            val param = classifier as KTypeParameter
            generics[param.name]!!
        }
    }

    /**
     * Solve the generic types based on the provided type parameters, arguments, and generics.
     *
     * @param types The list of type parameters.
     * @param arguments The list of type arguments.
     * @param generics The map of generic types.
     *
     * @return A map of solved generic types.
     */
    private fun solveGeneric(
        types: List<KTypeParameter>,
        arguments: List<KType>,
        generics: Map<String, RClass<*>>
    ): Map<String, RClass<*>> {
        val copyGeneric = mutableMapOf<String, RClass<*>>()
        for ((index, param) in types.withIndex()) {
            val name = param.name
            //val nullable =   param.upperBounds.any { it.isMarkedNullable }
            val rClass = if (index >= arguments.size)
                generics[name]!!
            else {
                val typ = solveType(arguments[index], generics)
                val nullable = arguments[index].isMarkedNullable
                if (typ.nullable != nullable)
                    typ.withNullable(nullable = nullable)
                else
                    typ
            }
            copyGeneric[name] = rClass
        }
        return copyGeneric
    }

}
