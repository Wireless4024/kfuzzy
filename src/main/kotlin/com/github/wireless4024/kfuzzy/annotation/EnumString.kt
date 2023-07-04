package com.github.wireless4024.kfuzzy.annotation

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class EnumString(vararg val value: String)
