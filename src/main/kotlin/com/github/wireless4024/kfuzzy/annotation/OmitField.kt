package com.github.wireless4024.kfuzzy.annotation

/**
 * By default, nullable property will omit field
 * @constructor Create empty Emit field
 */
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class OmitField(val value: Boolean = true)
