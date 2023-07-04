package com.github.wireless4024.kfuzzy.task

data class FailedTask(
    val task: ExecutableTask,
    val error: Throwable?,
    val payload: Any?
)
