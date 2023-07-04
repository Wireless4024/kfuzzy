package com.github.wireless4024.kfuzzy.task

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.atomic.AtomicLong

class TaskScheduler(
    val ctx: TaskContext,
    val scope: CoroutineScope,
) {
    private val remain = AtomicLong()
    private val queue = LinkedBlockingQueue<ExecutableTask>()

    suspend fun runTask(task: ExecutableTask) {
        val (dependent, independent) = task.execute(ctx)
        scope.launch {
            for (executableTask in independent) {
                runTask(executableTask)
            }
        }
        val dependentList = dependent.toList()
        remain.addAndGet(dependentList.size.toLong())
        queue += dependent
    }

    suspend fun run() {
        while (remain.get() > 0) {
            val task = queue.poll()
            runTask(task)
        }
    }
}
