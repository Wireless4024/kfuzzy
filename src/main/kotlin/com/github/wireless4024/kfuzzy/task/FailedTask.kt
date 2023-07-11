package com.github.wireless4024.kfuzzy.task

data class FailedTask(
    val ctx: CurrentContext,
    val error: Throwable?
)
