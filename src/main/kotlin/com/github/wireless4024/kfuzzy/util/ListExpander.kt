package com.github.wireless4024.kfuzzy.util


class ListExpander(private val list: List<List<Any?>>) : Iterator<List<Any?>> {
    private val index = mutableListOf<Int>()

    init {
        for (ignored in list.indices) {
            index.add(0)
        }
    }

    override fun hasNext(): Boolean {
        return list.isNotEmpty() && index.first() < list.first().size
    }

    override fun next(): List<Any?> {
        val values = mutableListOf<Any?>()
        for ((idx, value) in index.withIndex()) {
            val tmpList = list[idx]
            if (tmpList.isEmpty()) continue
            values += tmpList[value]
        }
        advanceIndex()
        return values
    }

    private fun advanceIndex() {
        var last = index.lastIndex
        while (last >= 0 && ++index[last] >= list[last].size) {
            if (last != 0)
                index[last--] = 0
            else
                break
        }
    }
}
