package com.github.wireless4024.kfuzzy.task

import com.github.wireless4024.kfuzzy.faker.FakerImpl
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.http.*
import io.ktor.util.reflect.*

class RestJob(val client: HttpClient = HttpClient(CIO) { expectSuccess = true }, initBlock: RestJob.() -> Unit) {
    private val context = TaskContext()
    val jobs = mutableListOf<RestTask<*>>()

    init {
        initBlock(this)
    }

    inline fun <reified R : Any> get(url: String, noinline block: CurrentContext.(R) -> Unit) {
        jobs.add(RestTask(url, HttpMethod.Get, typeInfo<R>(), callback = block))
    }

    suspend fun run(): Sequence<CurrentContext> {
        val contexts = mutableListOf<List<CurrentContext>>()
        try {
            for (job in jobs) {
                contexts += job.run(context, FakerImpl, client)
            }
        } catch (_: Throwable) { }
        return contexts.asSequence().flatten()
    }

    inline fun <reified T : Any, reified E : Any> post(
        url: String,
        noinline block: CurrentContext.(E) -> Unit
    ) {
        jobs.add(RestTask(url, HttpMethod.Post, typeInfo<E>(), typeInfo<T>(), { it }, block))
    }

    inline fun <reified T : Any, reified E : Any, reified R : Any> post(
        url: String,
        noinline transformer: (T) -> E,
        noinline block: CurrentContext.(R) -> Unit
    ) {
        @Suppress("UNCHECKED_CAST")
        jobs.add(RestTask(url, HttpMethod.Post, typeInfo<R>(), typeInfo<T>(), transformer as (Any?) -> Any?, block))
    }
}
