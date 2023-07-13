package com.github.wireless4024.kfuzzy.report

import com.github.wireless4024.kfuzzy.task.CurrentContext
import kotlinx.html.*
import kotlinx.html.stream.appendHTML
import kotlin.time.Duration

object ReportGenerator {
    fun generateReport(data: Iterable<CurrentContext>, start: Long, duration: Duration): String {
        val interval = duration.inWholeMilliseconds / 30

        val res = data.groupBy { (it["start"] as? Long)?.div(interval) }
            .entries
            .associateBy { it.key?.times(interval)?.minus(start) }
            .mapValues { it.value.value }
        res.forEach(::println)

        return buildString {
            appendLine("<!DOCTYPE html>")
            appendHTML().html {
                body {
                    div {
                        canvas {
                            id = "myChart"

                        }
                    }
                    script(src = "https://cdn.jsdelivr.net/npm/chart.js") {}
                    script {
                        unsafe {
                            +"""
                            const ctx = document.getElementById('myChart');
                            
                            new Chart(ctx, {
                                type: 'bar',
                                data: {
                                  labels: [
                             """.trimIndent()

                            +res.keys
                                .asSequence()
                                .map { "+${it}ms" }
                                .joinToString("\",\"", prefix = "\"", postfix = "\"")

                            +"""],
                                  datasets: [{
                                    label: '# of Task',
                                    data: [
                             """.trimIndent()

                            +res.values
                                .asSequence()
                                .map { it.size }
                                .joinToString(",")

                            +"""],
                                    borderWidth: 1
                                  }]
                                },
                                options: {
                                  scales: {
                                    y: {
                                      beginAtZero: true
                                    }
                                  }
                                }
                            });
                            """.trimIndent()
                        }
                    }
                }
            }
            appendLine()
        }
    }
}
