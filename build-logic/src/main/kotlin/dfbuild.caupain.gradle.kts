import com.deezer.caupain.plugin.DependenciesUpdateTask
import com.deezer.caupain.policies.StabilityLevelPolicy

plugins {
    alias(conventions.plugins.dfbuild.base)

    alias(libs.plugins.caupain)
}

caupain {
    showVersionReferences = true
    repositories {
        libraries = libraries.get()
            .filterNot { it.url == "https://redirector.kotlinlang.org/maven/bootstrap" }
    }
    outputs {
        markdown { enabled = true }
    }
    System.getenv("CAUPAIN_GITHUB_TOKEN")?.let {
        githubToken = it
        searchReleaseNote = true
    }
}

tasks.withType<DependenciesUpdateTask> {
    selectIf {
        StabilityLevelPolicy.select(dependency, currentVersion, updatedVersion) &&
            !updatedVersion.text.matches("^.*-preview([0-9]+)?$".toRegex())
    }
}
