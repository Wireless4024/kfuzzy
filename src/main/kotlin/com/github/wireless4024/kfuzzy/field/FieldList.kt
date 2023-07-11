package com.github.wireless4024.kfuzzy.field

import com.github.wireless4024.kfuzzy.faker.IFaker
import com.github.wireless4024.kfuzzy.generator.Generator
import com.github.wireless4024.kfuzzy.util.DeepToString.Companion.classToDeepString
import com.github.wireless4024.kfuzzy.util.ListCombinator
import com.github.wireless4024.kfuzzy.util.ListExpander

data class FieldList(val kind: FieldKind, val emitEmpty: Boolean = false, val generators: List<Generator>) : FieldKind {
    override fun randomValue(faker: IFaker): Any {
        return listOf(generators.random().successCase(kind, faker))
    }

    override fun possibleSuccessValues(faker: IFaker): List<Any?> {
        val possibleList = ListExpander(generators.map { it.possibleSuccessCase(kind, faker) })
            .asSequence()
            .filter { it.firstOrNull() != OmittedField }
            .toMutableList()

        return ListCombinator.generateList(possibleList, emitEmpty = !emitEmpty)
    }

    override fun possibleFailValues(faker: IFaker): List<Any?> {
        val failList = ListExpander(generators.map { it.possibleFailCase(kind, faker) })
            .asSequence()
            .filter { it.firstOrNull() != OmittedField }
            .toMutableList()

        return ListCombinator.generateList(failList, true, emitEmpty)
    }

    override fun toString(deep: Int) = classToDeepString(deep) {
        name("FieldList")
            .field("kind", kind)
            .field("emitEmpty", emitEmpty)
            .field("generators", generators)
    }
}
