package dfbuild

import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.attributes.Attribute
import org.gradle.api.component.AdhocComponentWithVariants
import org.gradle.api.component.SoftwareComponent
import org.gradle.api.component.SoftwareComponentFactory
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.TaskProvider
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.extra
import org.gradle.kotlin.dsl.newInstance
import javax.inject.Inject

/**
 * The name of the software component created by `dfbuild.kodex` when `libs.publisher` is applied.
 * It's the same as the `java` component, but with the KoDEx-processed jar and sources jar.
 *
 * Use [componentToPublish] to select it in `kotlinPublications {}`.
 */
const val KODEX_COMPONENT_NAME = "kodexJava"

private const val KODEX_COMPONENT_USED = "dfbuild.kodex.componentUsed"

/**
 * Returns the component that should be published for this project, for use in
 * ```kotlin
 * kotlinPublications {
 *     publication {
 *         composeOf { from(componentToPublish()) }
 *     }
 * }
 * ```
 * This is the `kodexJava` component (with the KoDEx-processed jar and sources jar) if `dfbuild.kodex` is applied
 * and created it, or the normal `java` component otherwise (like when `-PskipKodex` is used).
 */
fun Project.componentToPublish(): SoftwareComponent {
    val kodexComponent = components.findByName(KODEX_COMPONENT_NAME)
    if (kodexComponent != null) {
        extra[KODEX_COMPONENT_USED] = true
        return kodexComponent
    }
    return components.getByName("java")
}

/** Whether [componentToPublish] returned the `kodexJava` component. */
internal val Project.isKodexComponentUsed: Boolean
    get() = extra.has(KODEX_COMPONENT_USED) && extra[KODEX_COMPONENT_USED] == true

internal abstract class SoftwareComponentFactoryProvider @Inject constructor(val factory: SoftwareComponentFactory)

/**
 * Creates and registers the (still empty) [KODEX_COMPONENT_NAME] component.
 * Its variants must be added later using [addKodexVariants], when the `java` component is complete.
 */
internal fun Project.createKodexComponent(): AdhocComponentWithVariants {
    val factory = objects.newInstance<SoftwareComponentFactoryProvider>().factory
    return factory.adhoc(KODEX_COMPONENT_NAME).also { components.add(it) }
}

/**
 * Adds the same variants as the `java` component has to [component], but with the
 * KoDEx-processed [jar] (for `apiElements` and `runtimeElements`) and [sourcesJar] (for `sourcesElements`).
 * The attributes, dependencies, capabilities, and Maven scopes stay the same.
 * The `javadocElements` variant is added unchanged.
 */
internal fun Project.addKodexVariants(
    component: AdhocComponentWithVariants,
    jar: TaskProvider<out Jar>,
    sourcesJar: TaskProvider<out Jar>,
) {
    fun addVariant(originalName: String, artifactTask: TaskProvider<out Jar>?, classifier: String?) {
        val original = configurations.findByName(originalName) ?: return
        val variant = if (artifactTask == null) original else copyWithArtifact(original, artifactTask, classifier)
        component.addVariantsFromConfiguration(variant) {
            // same mapping as the `java` component does
            when (originalName) {
                JavaPlugin.API_ELEMENTS_CONFIGURATION_NAME -> mapToMavenScope("compile")
                JavaPlugin.RUNTIME_ELEMENTS_CONFIGURATION_NAME -> mapToMavenScope("runtime")
                else -> mapToOptional()
            }
        }
    }

    addVariant(JavaPlugin.API_ELEMENTS_CONFIGURATION_NAME, jar, null)
    addVariant(JavaPlugin.RUNTIME_ELEMENTS_CONFIGURATION_NAME, jar, null)
    addVariant(JavaPlugin.JAVADOC_ELEMENTS_CONFIGURATION_NAME, null, "javadoc")
    addVariant(JavaPlugin.SOURCES_ELEMENTS_CONFIGURATION_NAME, sourcesJar, "sources")
}

/**
 * Creates a non-consumable copy of the [original] outgoing configuration (so it's only used for publishing)
 * with the same attributes, dependencies, and capabilities, but with [artifactTask] as artifact.
 */
private fun Project.copyWithArtifact(
    original: Configuration,
    artifactTask: TaskProvider<out Jar>,
    classifier: String?,
): Configuration =
    configurations.maybeCreate("kodex${original.name.replaceFirstChar { it.uppercase() }}").apply {
        description = "KoDEx-processed version of '${original.name}', used for publishing."
        isCanBeConsumed = false
        isCanBeResolved = false
        extendsFrom(*original.extendsFrom.toTypedArray())
        dependencies.addAllLater(provider { original.dependencies })
        dependencyConstraints.addAllLater(provider { original.dependencyConstraints })
        for (key in original.attributes.keySet()) {
            @Suppress("UNCHECKED_CAST")
            attributes.attributeProvider(
                key as Attribute<Any>,
                provider { original.attributes.getAttribute(key)!! },
            )
        }
        original.outgoing.capabilities.forEach { outgoing.capability(it) }
        outgoing.artifact(artifactTask) {
            builtBy(artifactTask)
            // publish with the same classifier as the original artifact
            this.classifier = classifier
        }
    }
