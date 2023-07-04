package com.github.wireless4024.kfuzzy.annotation

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class EnumInteger(vararg val value: Long)
