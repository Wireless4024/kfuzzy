package com.github.wireless4024.kfuzzy.field

import com.github.wireless4024.kfuzzy.faker.IFaker
import kotlin.random.Random

class FieldInteger(private val min: Long, private val max: Long) : FieldKind {
    override fun randomValue(faker: IFaker) = Random.nextLong(min, max + 1)
    override fun possibleSuccessValues(faker: IFaker): List<Any?> = setOf(min, max).toList() // unique
    override fun possibleFailValues(faker: IFaker): List<Any?> {
        val failList = mutableListOf<Long>()
        if (min > Int.MIN_VALUE) failList += min - 1
        if (max != min) failList += max + 1
        return failList
    }
}
