package com.github.wireless4024.kfuzzy.field

import com.github.wireless4024.kfuzzy.faker.IFaker
import com.github.wireless4024.kfuzzy.util.DeepToString.Companion.classToDeepString

data class FieldEnumString(private val values: Array<out String>) : FieldKind {
    override fun randomValue(faker: IFaker) = values.random()
    override fun possibleSuccessValues(faker: IFaker) = values.asList()
    override fun possibleFailValues(faker: IFaker) = listOf("__INVALID_ENUM_FIELD__")

    override fun toString(deep: Int) = classToDeepString(deep) {
        name("FieldEnumString")
            .field("values", values)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is FieldEnumString) return false

        if (!values.contentEquals(other.values)) return false

        return true
    }

    override fun hashCode(): Int {
        return values.contentHashCode()
    }
}
