package com.github.wireless4024.kfuzzy.schema

import com.github.wireless4024.kfuzzy.annotation.*
import com.github.wireless4024.kfuzzy.field.*
import com.github.wireless4024.kfuzzy.generator.*
import com.github.wireless4024.kfuzzy.reflection.RClass
import com.github.wireless4024.kfuzzy.reflection.RField
import kotlin.reflect.full.isSubclassOf

object SchemaExtractor {
    fun extractAnnotation(
        nullable: Boolean,
        optional: Boolean? = null,
        annotations: List<Annotation>
    ): MutableList<Generator> {
        val generators = mutableListOf<Generator>()
        var omitField: Boolean? = optional
        var generateNotNull = false
        for (annotation in annotations) {
            when (annotation) {
                is Blank -> generators += BlankGenerator
                is NotBlank -> generators += if (nullable) NotBlankOrNullGenerator else NotBlankGenerator
                is EnumString -> generators += EnumStringGenerator(annotation.value)
                is OmitField -> {
                    generateNotNull = true
                    omitField = annotation.value
                }
                // is RangeInteger -> generators += RangeIntegerGenerator(annotation.min, annotation.max)
            }
        }

        if ((generators.isEmpty() || generateNotNull) && !nullable) {
            generators += NotNullGenerator
        } else if (nullable) {
            generators += NullableGenerator
        }

        if (omitField != false)
            generators += OmitFieldGenerator

        return generators
    }

    fun fromRClass(rClass: RClass<*>): Map<String, SchemaField> {
        val fields = mutableMapOf<String, SchemaField>()

        for (field in rClass.fields) {
            fields[field.name] = fromRField(field)
        }

        return fields
    }

    private fun fromRField(rField: RField): SchemaField {
        val (kind, generators) = kindOf(rField)
        return SchemaField(kind, generators)
    }

    private fun kindOf(rField: RField): Pair<FieldKind, List<Generator>> {
        val type = rField.type
        val inner = type.inner
        return when {
            /*inner.isSubclassOf(Collection::class) -> FieldList(
                kindOf(rField.type.generics.values.first(), rField),
                generators
            ) to extractAnnotation(rField.nullable, rField.annotations)*/

            inner.isSubclassOf(Enum::class) -> {
                val generators = extractAnnotation(rField.nullable, rField.optional, rField.annotations)
                generators.removeIf { it is NotNullGenerator }
                generators += EnumStringGenerator(type.enumValues!!.map { it.toString() }.toTypedArray())
                FieldString to generators
            }

            else -> kindOf(type, rField) to extractAnnotation(rField.nullable, rField.optional, rField.annotations)
        }
    }

    private fun kindOf(rClass: RClass<*>, parent: RField?): FieldKind {
        return if (rClass.isFlat) {
            when (rClass.inner) {
                String::class -> FieldString
                Boolean::class -> FieldBoolean
                else -> {
                    when {
                        rClass.inner.isSubclassOf(Number::class) -> {
                            val range = parent?.inner?.annotations?.find { it is RangeInteger } as? RangeInteger
                            FieldInteger(
                                range?.min ?: 0, range?.max ?: Int.MAX_VALUE.toLong()
                            )
                        }

                        rClass.inner.isSubclassOf(Collection::class) -> {
                            val inner = rClass.generics.values.first()
                            val emitEmpty = parent?.annotations?.find { it is NotBlank }
                            FieldList(
                                kindOf(inner, null),
                                emitEmpty != null,
                                extractAnnotation(
                                    parent?.nullable == true || inner.nullable,
                                    parent?.optional,
                                    inner.annotations
                                )
                            )
                        }

                        else -> throw UnsupportedOperationException()
                    }
                }
            }
        } else {
            FieldObject(Schema(fromRClass(rClass)))
        }
    }
}
