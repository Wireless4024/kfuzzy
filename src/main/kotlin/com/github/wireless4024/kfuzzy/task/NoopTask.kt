package com.github.wireless4024.kfuzzy.task

object NoopTask : ExecutableTask {
    override suspend fun execute(ctx: TaskContext): TaskResult = TaskResult(listOf(), listOf())
}
