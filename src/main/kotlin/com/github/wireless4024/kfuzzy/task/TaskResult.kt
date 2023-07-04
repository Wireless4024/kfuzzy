package com.github.wireless4024.kfuzzy.task

data class TaskResult(
    val dependentTasks: Iterable<ExecutableTask>,
    val independentTasks: Iterable<ExecutableTask>,
)
