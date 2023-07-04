package com.github.wireless4024.kfuzzy.field

import com.github.wireless4024.kfuzzy.faker.IFaker
import com.github.wireless4024.kfuzzy.util.DeepToString.Companion.toDeepString

data class FieldEnumString(private val values: Array<out String>) : FieldKind {
    override fun randomValue(faker: IFaker) = values.random()
    override fun possibleSuccessValues(faker: IFaker) = values.asList()
    override fun possibleFailValues(faker: IFaker) = listOf("\u0000")

    override fun toString(deep: Int): String {
        val parentIndent = "  ".repeat(deep)
        val indent = "  ".repeat(deep + 1)
        return "FieldEnumString(\n" +
                "${indent}values=${values.asList().toDeepString(deep + 1)}\n" +
                "${parentIndent})"
    }
}
