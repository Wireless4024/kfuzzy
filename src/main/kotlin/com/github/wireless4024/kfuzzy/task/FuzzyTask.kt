package com.github.wireless4024.kfuzzy.task

import com.github.wireless4024.kfuzzy.schema.Schema
import com.github.wireless4024.kfuzzy.util.DeepToString

class FuzzyTask<T>(
    private val schema: Schema,
    private val mapper: (Map<String, Any?>) -> T,
    init: () -> Unit = {},
    private val runner: suspend TaskContext.(T) -> Unit
) : ExecutableTask, DeepToString {
    private val nextList = mutableListOf<ExecutableTask>()
    private val transformers = mutableListOf<(T) -> T>({ it })

    init {
        init()
    }

    fun next(task: ExecutableTask) {
        nextList += task
    }

    fun addTransformer(transformer: (T) -> T) {
        transformers += transformer
    }

    override suspend fun execute(ctx: TaskContext): TaskResult {
        val owner = this
        val tasks = mutableListOf<ExecutableTask>()

        for (transformer in transformers) {
            tasks += JoiningTask(ctx.spawnSuccess(owner, transformer(mapper(schema.successCase(ctx.faker))), runner))
            tasks += nextList

            for (it in schema.possibleSuccessCase(ctx.faker)) {
                ctx.spawnSuccess(owner, transformer(mapper(it)), runner)
            }

            for (it in schema.possibleFailCase(ctx.faker)) {
                ctx.spawnFailure(owner, transformer(mapper(it)), runner)
            }
        }

        return TaskResult(nextList, listOf())
    }

    override fun toString(): String {
        return toString(0)
    }

    override fun toString(deep: Int): String {
        val parentIndent = "  ".repeat(deep)
        val indent = "  ".repeat(deep + 1)
        return "FuzzyTask(\n" +
                "${indent}schema=${schema.toString(deep + 1)}\n" +
                "${parentIndent})"
    }
}
