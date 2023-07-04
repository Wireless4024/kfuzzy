package com.github.wireless4024.kfuzzy.task

import kotlinx.coroutines.Job

class JoiningTask(private val job: Job) : ExecutableTask {
    override suspend fun execute(ctx: TaskContext): TaskResult {
        job.join()
        return TaskResult(listOf(), listOf())
    }
}
