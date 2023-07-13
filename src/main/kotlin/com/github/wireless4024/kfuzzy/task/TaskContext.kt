package com.github.wireless4024.kfuzzy.task

import com.github.wireless4024.kfuzzy.faker.FakerImpl
import com.github.wireless4024.kfuzzy.faker.IFaker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.async
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicLong
import kotlin.reflect.KClass

class TaskContext : AutoCloseable {
    private val executor = Executors.newWorkStealingPool()
    private val scope = CoroutineScope(executor.asCoroutineDispatcher())
    private val storage = ConcurrentHashMap<Any, Any?>()
    private val concurrency = AtomicLong()

    var faker: IFaker = FakerImpl

    fun spawn(ctx: CurrentContext, block: suspend CurrentContext.() -> Unit): Deferred<CurrentContext> {
        concurrency.getAndIncrement()
        return scope.async {
            ctx["start"] = System.currentTimeMillis()
            ctx["concurrency"] = concurrency.get()
            block(ctx)
            ctx["end"] = System.currentTimeMillis()
            concurrency.getAndDecrement()
            ctx
        }
    }

    fun <T> spawnSuccess(
        ctx: CurrentContext,
        block: suspend CurrentContext.(T) -> Unit
    ): Deferred<CurrentContext> {
        return spawn(ctx) {
            try {
                @Suppress("UNCHECKED_CAST") val value = body as T
                block(value)
            } catch (e: Throwable) {
                fail(e)
            }
        }
    }

    fun <T> spawnFailure(
        ctx: CurrentContext,
        block: suspend CurrentContext.(T) -> Unit
    ): Deferred<CurrentContext> {
        return spawn(ctx) {
            try {
                @Suppress("UNCHECKED_CAST") val value = body as T
                block(value)
                fail(IllegalStateException("Task with payload $value should not success"))
            } catch (_: Throwable) {
            }
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
        return "TaskContext(storage=$storage, faker=$faker)"
    }

    override fun close() {}
}
