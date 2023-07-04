package com.github.wireless4024.kfuzzy.field

import com.github.wireless4024.kfuzzy.faker.IFaker
import com.github.wireless4024.kfuzzy.util.DeepToString


interface FieldKind : DeepToString {
    fun randomValue(faker: IFaker): Any?
    fun possibleSuccessValues(faker: IFaker): List<Any?> = listOf(randomValue(faker))
    fun possibleFailValues(faker: IFaker): List<Any?> = listOf()
}
