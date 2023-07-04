package com.github.wireless4024.kfuzzy.task

import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.atomic.AtomicLong

data class TaskStats(
    val success: AtomicLong = AtomicLong(),
    val failure: LinkedBlockingDeque<FailedTask> = LinkedBlockingDeque(),
)
