package com.github.wireless4024.kfuzzy.generator

import com.github.wireless4024.kfuzzy.faker.IFaker
import com.github.wireless4024.kfuzzy.field.FieldKind

object NullableGenerator : Generator {
    override fun successCase(kind: FieldKind, faker: IFaker) = null
    override fun possibleSuccessCase(kind: FieldKind, faker: IFaker) =
        kind.possibleSuccessValues(faker) + listOf(null)

    override fun possibleFailCase(kind: FieldKind, faker: IFaker) = kind.possibleFailValues(faker)
}
