package com.github.wireless4024.kfuzzy.generator

import com.github.wireless4024.kfuzzy.faker.IFaker
import com.github.wireless4024.kfuzzy.field.FieldKind

object NotNullGenerator : Generator {
    override fun successCase(kind: FieldKind, faker: IFaker) = kind.randomValue(faker)
    override fun possibleSuccessCase(kind: FieldKind, faker: IFaker) = kind.possibleSuccessValues(faker)
    override fun possibleFailCase(kind: FieldKind, faker: IFaker) =
        kind.possibleFailValues(faker) + listOf(null)
}
