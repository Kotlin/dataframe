@file:Suppress("UnstableApiUsage")

package org.jetbrains.dataframe.gradle

import org.gradle.api.Project
import org.gradle.api.internal.project.DefaultProject
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.api.provider.Provider
import org.gradle.build.event.BuildEventsListenerRegistry
import org.gradle.internal.service.DefaultServiceRegistry
import org.gradle.internal.service.scopes.ProjectScopeServices
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.tooling.events.OperationCompletionListener
import java.lang.reflect.Field
import java.util.concurrent.atomic.AtomicReference

internal fun makeProject(): ProjectInternal {
    val project = ProjectBuilder.builder().build() as ProjectInternal
    addBuildEventsListenerRegistryMock(project)
    return project
}

/**
 * In Gradle 6.7-rc-1 BuildEventsListenerRegistry service is not created in we need it in order
 * to instantiate AGP. This creates a fake one and injects it - http://b/168630734.
 */
internal fun addBuildEventsListenerRegistryMock(project: Project) {
    try {
        val projectScopeServices = (project as DefaultProject).services as ProjectScopeServices
        val state: Field = ProjectScopeServices::class.java.superclass.getDeclaredField("state")
        state.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        val stateValue: AtomicReference<Any> = state.get(projectScopeServices) as AtomicReference<Any>
        val enumClass = Class.forName(DefaultServiceRegistry::class.java.name + "\$State")
        stateValue.set(enumClass.enumConstants[0])

        // add service and set state so that future mutations are not allowed
        projectScopeServices.add(BuildEventsListenerRegistry::class.java, BuildEventsListenerRegistryMock)
        stateValue.set(enumClass.enumConstants[1])
    } catch (e: Throwable) {
        throw RuntimeException(e)
    }
}

object BuildEventsListenerRegistryMock : BuildEventsListenerRegistry {

    override fun onTaskCompletion(listener: Provider<out OperationCompletionListener>?) {
    }

}
