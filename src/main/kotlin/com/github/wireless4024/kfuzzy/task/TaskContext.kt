package com.github.wireless4024.kfuzzy.task

import com.github.wireless4024.kfuzzy.faker.FakerImpl
import com.github.wireless4024.kfuzzy.faker.IFaker
import kotlinx.coroutines.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
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

    private fun panic(cause: String) {
        //scope.cancel(cause)
    }

    fun <T> spawnSuccess(ctx: CurrentContext, block: suspend CurrentContext.(T) -> Unit): Deferred<CurrentContext> {
        return scope.async {
            try {
                @Suppress("UNCHECKED_CAST") val value = ctx.body as T
                block(ctx, value)
                taskStats.success.getAndIncrement()
            } catch (e: Throwable) {
                taskStats.failure += FailedTask(ctx, e)
                ctx.fail(e)
            }
            ctx
        }
    }

    fun <T> spawnFailure(ctx: CurrentContext, block: suspend CurrentContext.(T) -> Unit): Deferred<CurrentContext> {
        return scope.async {
            try {
                @Suppress("UNCHECKED_CAST") val value = ctx.body as T
                block(ctx, value)
                taskStats.failure += FailedTask(ctx, null)
                ctx.fail(IllegalStateException("Task with payload $value should not success"))
            } catch (e: Throwable) {
                taskStats.success.getAndIncrement()
            }
            ctx
        }
    }

    fun newContext() = CurrentContext(this)

    suspend fun run(task: ExecutableTask) {
        TaskScheduler(this, scope).apply {
            runTask(task)
            run()
        }
    }

    @Synchronized
    fun <T> getOrDefault(key: String, default: (key: Any) -> T): MutableSet<T> {
        storage.computeIfAbsent(key, default)
        return get(key)
    }

    internal fun <T> set(key: String, value: T) {
        storage[key] = value
    }

    internal fun <T : Any> set(key: KClass<T>, value: T) {
        storage[key] = value
    }

    internal fun <T> get(key: String): T {
        @Suppress("UNCHECKED_CAST")
        return storage[key] as T
    }

    internal fun <T : Any> get(key: KClass<T>): T {
        @Suppress("UNCHECKED_CAST")
        return storage[key] as T
    }

    override fun toString(): String {
        return "TaskContext(storage=$storage, taskStats=$taskStats, faker=$faker)"
    }

    override fun close() {}
}
