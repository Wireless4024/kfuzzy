package com.github.wireless4024.kfuzzy.generator

import com.github.wireless4024.kfuzzy.faker.IFaker
import com.github.wireless4024.kfuzzy.field.FieldKind
import com.github.wireless4024.kfuzzy.field.OmittedField

object OmitFieldGenerator : Generator {
    override fun successCase(kind: FieldKind, faker: IFaker) = OmittedField
    override fun possibleSuccessCase(kind: FieldKind, faker: IFaker) = listOf(OmittedField)
    override fun possibleFailCase(kind: FieldKind, faker: IFaker) = listOf<Any>()
}
