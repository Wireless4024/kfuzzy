package com.github.wireless4024.kfuzzy.generator

import com.github.wireless4024.kfuzzy.faker.IFaker
import com.github.wireless4024.kfuzzy.field.FieldKind
import com.github.wireless4024.kfuzzy.field.FieldString

object NotBlankGenerator : Generator {
    override fun successCase(kind: FieldKind, faker: IFaker) = kind.randomValue(faker)
    override fun possibleSuccessCase(kind: FieldKind, faker: IFaker) = listOf<Any>()
    override fun possibleFailCase(kind: FieldKind, faker: IFaker) =
        if (kind is FieldString)
            listOf("")
        else
            listOf()
}
