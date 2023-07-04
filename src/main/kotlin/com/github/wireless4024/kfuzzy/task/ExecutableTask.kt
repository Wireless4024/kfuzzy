package com.github.wireless4024.kfuzzy.task

interface ExecutableTask {
    suspend fun execute(ctx: TaskContext): TaskResult
}

