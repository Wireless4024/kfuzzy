package com.github.wireless4024.kfuzzy.task

import com.github.wireless4024.kfuzzy.faker.FakerImpl
import com.github.wireless4024.kfuzzy.faker.IFaker
import kotlinx.coroutines.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass

class TaskContext : AutoCloseable {
    private val executor = Executors.newWorkStealingPool()
    private val scope = CoroutineScope(executor.asCoroutineDispatcher())
    private val storage = ConcurrentHashMap<Any, Any?>()

    val taskStats = TaskStats()
    var faker: IFaker = FakerImpl

    fun spawn(block: suspend TaskContext.() -> Unit): Job {
        return scope.launch { block(this@TaskContext) }
    }

    fun spawnBlocking(block: TaskContext.() -> Unit): Job {
        return scope.launch(Dispatchers.IO) { block(this@TaskContext) }
    }

    fun <T> spawnSuccess(owner: ExecutableTask, value: T, block: suspend TaskContext.(T) -> Unit): Job {
        return scope.launch {
            try {
                block(this@TaskContext, value)
                taskStats.success.getAndIncrement()
            } catch (e: Throwable) {
                scope.cancel("Error")
                e.printStackTrace()
                taskStats.failure += FailedTask(owner, e, value)
            }
        }
    }

    fun <T> spawnFailure(owner: ExecutableTask, value: T, block: suspend TaskContext.(T) -> Unit): Job {
        return scope.launch {
            try {
                block(this@TaskContext, value)
                taskStats.failure += FailedTask(owner, null, value)
                scope.cancel("Error")
            } catch (e: Throwable) {
                taskStats.success.getAndIncrement()
            }
        }
    }

    fun <T> successBlocking(owner: ExecutableTask, value: T, block: suspend TaskContext.(T) -> Unit): Job {
        return spawnSuccess(owner, value) {
            withContext(Dispatchers.IO) {
                block(this@TaskContext, value)
            }
        }
    }

    fun <T> failBlocking(owner: ExecutableTask, value: T, block: suspend TaskContext.(T) -> Unit): Job {
        return spawnFailure(owner, value) {
            withContext(Dispatchers.IO) {
                block(this@TaskContext, value)
            }
        }
    }

    suspend fun run(task: ExecutableTask) {
        TaskScheduler(this, scope).apply {
            runTask(task)
            run()
        }
    }

    infix fun String.with(value: Any) {
        storage[this] = value
    }

    fun <T> set(key: String, value: T) {
        storage[key] = value
    }

    fun <T : Any> set(key: KClass<T>, value: T) {
        storage[key] = value
    }

    fun <T> get(key: String): T {
        @Suppress("UNCHECKED_CAST")
        return storage[key] as T
    }

    fun <T : Any> get(key: KClass<T>): T {
        @Suppress("UNCHECKED_CAST")
        return storage[key] as T
    }

    override fun toString(): String {
        return "TaskContext(storage=$storage, taskStats=$taskStats, faker=$faker)"
    }

    override fun close() {
        executor.shutdown()
        executor.awaitTermination(5, TimeUnit.MINUTES)
    }
}
