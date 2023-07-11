package com.github.wireless4024.kfuzzy.task

import com.github.wireless4024.kfuzzy.faker.FakerImpl
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import io.ktor.util.reflect.*

class RestJob(
    val client: HttpClient = buildHttpServer(),
    initBlock: RestJob.() -> Unit
) {
    constructor(
        baseUrl: String,
        initBlock: RestJob.() -> Unit
    ) : this(buildHttpServer { defaultRequest { url(baseUrl) } }, initBlock)

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
        } catch (_: Throwable) {
        }
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

    companion object {
        @JvmOverloads
        fun buildHttpServer(block: (HttpClientConfig<CIOEngineConfig>.() -> Unit)? = null) = HttpClient(CIO) {
            expectSuccess = true
            install(ContentNegotiation) { jackson() }
            defaultRequest {
                contentType(ContentType.Application.Json)
            }
            block?.invoke(this)
        }
    }
}
