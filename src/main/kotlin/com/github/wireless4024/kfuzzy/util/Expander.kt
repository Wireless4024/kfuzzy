package com.github.wireless4024.kfuzzy.util

import com.github.wireless4024.kfuzzy.field.OmittedField


class Expander(list: Map<String, List<Any?>>) : Iterator<Map<String, Any?>> {
    private val pairs: List<Pair<String, List<Any?>>>
    private val index = mutableListOf<Int>()

    init {
        pairs = list.entries.map { it.key to it.value }
        for (ignored in list.keys.indices) {
            index.add(0)
        }
    }

    override fun hasNext(): Boolean {
        return pairs.isNotEmpty() && index.first() < pairs.first().second.size
    }

    override fun next(): Map<String, Any?> {
        val obj = mutableMapOf<String, Any?>()
        for ((idx, value) in index.withIndex()) {
            val v = pairs[idx].second.getOrNull(value)
            if (v == OmittedField) continue
            obj[pairs[idx].first] = v
        }
        advanceIndex()
        return obj
    }

    private fun advanceIndex() {
        var last = index.lastIndex
        while (last >= 0 && ++index[last] >= pairs[last].second.size) {
            if (last != 0)
                index[last--] = 0
            else
                break
        }
    }
}
