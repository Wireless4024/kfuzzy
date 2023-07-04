package com.github.wireless4024.kfuzzy.generator

import com.github.wireless4024.kfuzzy.faker.IFaker
import com.github.wireless4024.kfuzzy.field.FieldKind

interface Generator {
    fun successCase(kind: FieldKind, faker: IFaker): Any?
    fun possibleSuccessCase(kind: FieldKind, faker: IFaker): List<Any?>
    fun possibleFailCase(kind: FieldKind, faker: IFaker): List<Any?>
}
