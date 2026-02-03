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

public abstract class KeywordsGeneratorTask: DefaultTask() {

    @get:Inject
    public abstract val executor: WorkerExecutor

    @get:Classpath
    public abstract val kotlinCompiler: ConfigurableFileCollection

    @OutputDirectory
    public lateinit var srcDir: File

    @Input
    override fun getGroup(): String = "codegen"

    @TaskAction
    public fun generate() {
        val workQueue = executor.classLoaderIsolation {
            classpath.from(kotlinCompiler)
        }
        workQueue.submit(KeywordsGeneratorAction::class) {
            srcDir = this@KeywordsGeneratorTask.srcDir
        }
    }

    public companion object {
        public const val NAME: String = "generateKeywordsSrc"
    }
}
