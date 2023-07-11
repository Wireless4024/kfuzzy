package com.github.wireless4024.kfuzzy.util

object ListCombinator {
    fun generateList(list: List<List<Any?>>, expand: Boolean = false, emitEmpty: Boolean = false): List<List<Any?>> {
        if (list.size <= 1) {
            return if (emitEmpty)
                listOf(listOf<Any?>()) + list
            else
                list
        }

        return if (expand)
            powerSet(list, if (emitEmpty) 0 else 1)
        else
            generate(list, emitEmpty)
    }

    private fun generate(list: List<List<Any?>>, emitEmpty: Boolean = false): List<List<Any?>> {
        val output = mutableListOf<List<Any?>>()
        if (emitEmpty) output += listOf<Any?>()
        output.addAll(list)
        for (i in 2..list.size) {
            output.add(list.subList(0, i).map { it[0] })
        }
        return output
    }

    private fun powerSet(list: List<List<Any?>>, start: Int = 0): List<List<Any?>> {
        val powerSetSize = 1 shl list.size
        val result = mutableListOf<List<Any?>>()

        for (counter in start until powerSetSize) {
            val subset = mutableListOf<Any?>()

            for (j in list.indices) {
                if ((counter and (1 shl j)) > 0)
                    subset.add(list[j][0])
            }

            result.add(subset)
        }

        return result
    }
}
