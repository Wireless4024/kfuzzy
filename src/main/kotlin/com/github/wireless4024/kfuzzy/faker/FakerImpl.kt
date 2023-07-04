package com.github.wireless4024.kfuzzy.faker

import kotlin.random.Random

object FakerImpl : IFaker {
    private val ALPHABET = 'A'..'Z'
    override fun fakeString(): String {
        val len = Random.nextInt(5, 10)
        return (1..len).map { ALPHABET.random() }.joinToString("")
    }
}
