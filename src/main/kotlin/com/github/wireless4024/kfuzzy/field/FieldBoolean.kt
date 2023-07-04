package com.github.wireless4024.kfuzzy.field

import com.github.wireless4024.kfuzzy.faker.IFaker
import kotlin.random.Random

object FieldBoolean : FieldKind {
    override fun randomValue(faker: IFaker): Any {
        return Random.nextBoolean()
    }

    override fun possibleSuccessValues(faker: IFaker) = listOf(true, false)
}
