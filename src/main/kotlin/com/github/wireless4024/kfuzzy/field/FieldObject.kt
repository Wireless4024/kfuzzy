package com.github.wireless4024.kfuzzy.field

import com.github.wireless4024.kfuzzy.faker.IFaker
import com.github.wireless4024.kfuzzy.schema.Schema

data class FieldObject(val schema: Schema) : FieldKind {
    override fun randomValue(faker: IFaker): Any {
        return schema.successCase(faker)
    }

    override fun possibleSuccessValues(faker: IFaker) = schema.possibleSuccessCase(faker).toList()
    override fun possibleFailValues(faker: IFaker) = schema.possibleFailCase(faker).toList()

    override fun toString(deep: Int): String {
        val parentIndent = "  ".repeat(deep)
        val indent = "  ".repeat(deep + 1)
        return "FieldObject(\n" +
                "${indent}schema=${schema.toString(deep + 1)}\n" +
                "${parentIndent})"
    }
}
