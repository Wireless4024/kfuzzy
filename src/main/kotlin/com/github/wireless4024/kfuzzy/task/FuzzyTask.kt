package com.github.wireless4024.kfuzzy.task

import com.github.wireless4024.kfuzzy.schema.Schema
import com.github.wireless4024.kfuzzy.util.DeepToString
import com.github.wireless4024.kfuzzy.util.DeepToString.Companion.classToDeepString
import com.github.wireless4024.kfuzzy.wrapper.DynamicObject
import kotlinx.coroutines.Job
import kotlinx.coroutines.joinAll

class FuzzyTask<T>(
    private val schema: Schema,
    private val mapper: DynamicObject.() -> T,
    init: () -> Unit = {},
    private val runner: suspend CurrentContext.(T) -> Unit
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
                val currentContext = ctx.newContext()
                val value = mapper(it)
                currentContext.body = value
                if (failOn(value))
                    ctx.spawnFailure(currentContext, runner)
                else
                    tasks += ctx.spawnSuccess(currentContext, runner)
            }

            for (it in schema.possibleFailCase(ctx.faker)) {
                val currentContext = ctx.newContext()
                currentContext.body = transformer(mapper(it))
                ctx.spawnFailure(currentContext, runner)
            }
        }
        tasks.joinAll()

        return TaskResult(nextList, listOf())
    }


    override fun toString(deep: Int) = classToDeepString(deep) {
        name("FuzzyTask")
            .field("schema", schema)
    }
    override fun toString(): String {
        return "FuzzyTask(schema=$schema)"
    }
}
