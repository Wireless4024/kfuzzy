package com.github.wireless4024.kfuzzy.task

import com.github.wireless4024.kfuzzy.faker.IFaker
import com.github.wireless4024.kfuzzy.schema.Schema
import com.github.wireless4024.kfuzzy.wrapper.DynamicObject
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.util.reflect.*
import kotlinx.coroutines.awaitAll

class RestTask<R>(
    private val url: String,
    private val method: HttpMethod,
    private val responseType: TypeInfo,
    private val requestType: TypeInfo = typeInfo<Any>(),
    val transformer: (DynamicObject) -> Any? = { it },
    val callback: CurrentContext.(R) -> Unit
) {
    suspend fun run(context: TaskContext, faker: IFaker, client: HttpClient): List<List<CurrentContext>> {
        if (requestType.type == Any::class) {
            // these schemas generate nothing
            val currentContext = context.newContext()
            return listOf(listOf(context.spawnSuccess<Any>(currentContext) { send(this, client) }.await()))
        }

        val schema = Schema(requestType.type)
        val result = mutableListOf<List<CurrentContext>>()

        result += schema.possibleSuccessCase(faker)
            .map {
                val currentContext = context.newContext()
                currentContext.body = transformer(it)
                context.spawnSuccess<Any>(currentContext) { send(this, client) }
            }
            .toList()
            .awaitAll()

        result += schema.possibleFailCase(faker)
            .map {
                val currentContext = context.newContext()
                currentContext.body = transformer(it)
                context.spawnFailure<Any>(currentContext) { send(this, client) }
            }
            .toList()
            .awaitAll()

        return result
    }

    suspend fun send(context: CurrentContext, client: HttpClient) {
        val response = when (method) {
            HttpMethod.Get -> {
                client.get(url)
            }

            HttpMethod.Post -> {
                client.post(url) {
                    setBody(context.body, requestType)
                }
            }

            else -> throw UnsupportedOperationException("Unsupported http method $method")
        }

        // skip checking if return type is Unit
        if (responseType.type == Unit::class) return

        val responseBody = response.body<R>(responseType)
        verify(context, responseBody)
    }

    private fun verify(context: CurrentContext, responseBody: R) {
        context.body = responseBody
        callback(context, responseBody)
    }
}
