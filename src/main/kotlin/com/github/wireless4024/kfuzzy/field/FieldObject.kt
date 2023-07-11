package com.github.wireless4024.kfuzzy.field

import com.github.wireless4024.kfuzzy.faker.IFaker
import com.github.wireless4024.kfuzzy.schema.Schema
import com.github.wireless4024.kfuzzy.util.DeepToString.Companion.classToDeepString

data class FieldObject(val schema: Schema) : FieldKind {
    override fun randomValue(faker: IFaker): Any {
        return schema.successCase(faker)
    }

    override fun possibleSuccessValues(faker: IFaker) = schema.possibleSuccessCase(faker).toList()
    override fun possibleFailValues(faker: IFaker) = schema.possibleFailCase(faker).toList()

    override fun toString(deep: Int) = classToDeepString(deep) {
        name("FieldObject")
            .field("schema", schema)
    }
}
