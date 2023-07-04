package com.github.wireless4024.kfuzzy.generator

import com.github.wireless4024.kfuzzy.faker.IFaker
import com.github.wireless4024.kfuzzy.field.FieldKind

class EnumStringGenerator(private val values: Array<out String>) : Generator {
    override fun successCase(kind: FieldKind, faker: IFaker) = values.random()
    override fun possibleSuccessCase(kind: FieldKind, faker: IFaker) = values.asList()
    override fun possibleFailCase(kind: FieldKind, faker: IFaker) = listOf("__INVALID_ENUM_FIELD__")
}
