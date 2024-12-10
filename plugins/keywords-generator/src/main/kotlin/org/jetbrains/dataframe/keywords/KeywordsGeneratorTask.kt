package org.jetbrains.dataframe.keywords

import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.submit
import org.gradle.workers.WorkerExecutor
import java.io.File
import javax.inject.Inject

abstract class KeywordsGeneratorTask: DefaultTask() {

    @get:Inject
    abstract val executor: WorkerExecutor

    @get:Classpath
    abstract val kotlinCompiler: ConfigurableFileCollection

    @OutputDirectory
    lateinit var srcDir: File

    @Input
    override fun getGroup() = "codegen"

    @TaskAction
    fun generate() {
        val workQueue = executor.classLoaderIsolation {
            classpath.from(kotlinCompiler)
        }
        workQueue.submit(KeywordsGeneratorAction::class) {
            srcDir = this@KeywordsGeneratorTask.srcDir
        }
    }

    companion object {
        const val NAME = "generateKeywordsSrc"
    }
}
