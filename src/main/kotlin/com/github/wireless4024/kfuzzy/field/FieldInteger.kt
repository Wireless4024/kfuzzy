package com.github.wireless4024.kfuzzy.field

import com.github.wireless4024.kfuzzy.faker.IFaker
import kotlin.random.Random

class FieldInteger(private val min: Long, private val max: Long) : FieldKind {
    override fun randomValue(faker: IFaker) = Random.nextLong(min, max + 1)
    override fun possibleSuccessValues(faker: IFaker): List<Any?> = listOf(min, max)
    override fun possibleFailValues(faker: IFaker): List<Any?> {
        val failList = mutableListOf<Long>()
        if (min > Int.MIN_VALUE) Int.MIN_VALUE
        if (max < Int.MAX_VALUE) Int.MIN_VALUE
        return failList
    }
}
