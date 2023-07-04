package com.github.wireless4024.kfuzzy.annotation

@Target(AnnotationTarget.PROPERTY, AnnotationTarget.TYPE)
@Retention(AnnotationRetention.RUNTIME)
annotation class RangeInteger(
    val min: Long = Int.MIN_VALUE.toLong() - 1,
    val max: Long = Int.MAX_VALUE.toLong() - 1
)
