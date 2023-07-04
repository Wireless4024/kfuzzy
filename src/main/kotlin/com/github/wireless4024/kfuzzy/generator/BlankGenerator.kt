package com.github.wireless4024.kfuzzy.generator

import com.github.wireless4024.kfuzzy.faker.IFaker
import com.github.wireless4024.kfuzzy.field.FieldKind

object BlankGenerator : Generator {
    override fun successCase(kind: FieldKind, faker: IFaker) = ""
    override fun possibleSuccessCase(kind: FieldKind, faker: IFaker) = kind.possibleSuccessValues(faker) + listOf("")
    override fun possibleFailCase(kind: FieldKind, faker: IFaker) = kind.possibleFailValues(faker)
}
