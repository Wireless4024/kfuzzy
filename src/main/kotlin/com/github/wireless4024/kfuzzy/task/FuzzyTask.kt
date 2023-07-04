package com.github.wireless4024.kfuzzy.task

import com.github.wireless4024.kfuzzy.schema.Schema
import com.github.wireless4024.kfuzzy.util.DeepToString
import kotlinx.coroutines.Job
import kotlinx.coroutines.joinAll

class FuzzyTask<T>(
    private val schema: Schema,
    private val mapper: (Map<String, Any?>) -> T,
    init: () -> Unit = {},
    private val runner: suspend TaskContext.(T) -> Unit
) : ExecutableTask, DeepToString {
    private val nextList = mutableListOf<ExecutableTask>()
    private val transformers = mutableListOf<(T) -> T>({ it })
    private var failOn: (T) -> Boolean = { false }

    init {
        init()
    }

    fun next(task: ExecutableTask) {
        nextList += task
    }

    /**
     * Add a condition to mark a success case as a fail case
     *
     * @param condition
     */
    fun failOn(condition: (T) -> Boolean) {
        failOn = condition
    }

    /**
     * Add transformer to generate another case of test data.
     * For example, you want to change field `a` to null but schema can't generate it
     *
     * @param transformer
     * @receiver
     */
    fun addTransformer(transformer: (T) -> T) {
        transformers += transformer
    }

    override suspend fun execute(ctx: TaskContext): TaskResult {
        val owner = this
        val tasks = mutableListOf<Job>()

        for (transformer in transformers) {
            for (it in schema.possibleSuccessCase(ctx.faker)) {
                val value = mapper(it)
                if (failOn(value))
                    ctx.spawnFailure(owner, transformer(value), runner)
                else
                    tasks += ctx.spawnSuccess(owner, transformer(value), runner)
            }

            for (it in schema.possibleFailCase(ctx.faker)) {
                ctx.spawnFailure(owner, transformer(mapper(it)), runner)
            }
        }
        tasks.joinAll()

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
