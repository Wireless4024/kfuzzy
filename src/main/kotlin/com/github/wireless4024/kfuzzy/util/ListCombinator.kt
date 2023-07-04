package com.github.wireless4024.kfuzzy.util

import kotlin.random.Random

object ListCombinator {
    fun generateList(list: List<List<Any?>>, expand: Boolean = false): List<List<Any?>> {
        if (list.size == 1) return list
        val random = Random(1234567890)
        val indices = list.indices.toMutableList()

        val remainingIndex = indices.toMutableList()
        val output = mutableListOf<List<Any?>>()
        output.addAll(list)
        var last = list.lastIndex
        for (i in 2..list.size) {
            for (k in 1..last--) {
                val working = mutableListOf<Any?>()
                for (j in 1..i) {
                    val idx = random.nextInt(remainingIndex.size)
                    val pick = remainingIndex.removeAt(idx)
                    working += list[pick][0]
                }
                output += working
                remainingIndex.clear()
                remainingIndex.addAll(indices)
                if (!expand) break
            }
        }
        return output
    }
}
