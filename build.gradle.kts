import org.jetbrains.kotlinx.publisher.apache2
import org.jetbrains.kotlinx.publisher.developer
import org.jetbrains.kotlinx.publisher.githubRepo

plugins {
    with(convention.plugins) {
        alias(kotlinJvm8)
        alias(buildExampleProjects)
        alias(dependencyUpdates)
    }

    with(libs.plugins) {
        alias(publisher)
        alias(serialization) apply false
        alias(dokka)

        alias(simpleGit) apply false

        // dependence on our own obsolete KSP plugin
        // TODO remove when no examples need it anymore
        alias(dataframe.ksp) apply false
    }
}

val projectName: String by project

configurations {
    testImplementation.get().extendsFrom(compileOnly.get())
}

dependencies {
    api(projects.core)

    // expose all optional IO dependencies by default
    api(projects.dataframeArrow)
    api(projects.dataframeExcel)
    api(projects.dataframeJdbc)
    api(projects.dataframeCsv)
    api(projects.dataframeJson)

    // experimental, so not included by default:
    // api(projects.dataframeOpenapi)
}

group = "org.jetbrains.kotlinx"

fun detectVersion(): String {
    val buildNumber = rootProject.findProperty("build.number") as String?
    val versionProp = property("version") as String
    return if (hasProperty("release")) {
        versionProp
    } else if (buildNumber != null) {
        if (rootProject.findProperty("build.number.detection") == "true") {
            "$versionProp-dev-$buildNumber"
        } else {
            error("use build.number + build.number.detection = true or release build")
        }
    } else {
        "$versionProp-dev"
    }
}

val detectVersionForTC by tasks.registering {
    doLast {
        println("##teamcity[buildNumber '$version']")
    }
}

version = detectVersion()
println("Current DataFrame version: $version")

subprojects {
    this.version = rootProject.version
}

kotlinPublications {
    fairDokkaJars = false

    sonatypeSettings(
        project.findProperty("kds.sonatype.central.username") as String?,
        project.findProperty("kds.sonatype.central.password") as String?,
        "dataframe project, v. ${project.version}",
    )

    signingCredentials(
        project.findProperty("kds.sign.key.id") as String?,
        project.findProperty("kds.sign.key.private") as String?,
        project.findProperty("kds.sign.key.passphrase") as String?,
    )

    pom {
        githubRepo("Kotlin", "dataframe")
        inceptionYear = "2021"
        licenses {
            apache2()
        }
        developers {
            developer("koperagen", "Nikita Klimenko", "nikita.klimenko@jetbrains.com")
            developer("Jolanrensen", "Jolan Rensen", "jolan.rensen@jetbrains.com")
            developer("zaleslaw", "Aleksei Zinovev", "aleksei.zinovev@jetbrains.com")
            developer("ermolenkodev", "Nikita Ermolenko", "nikita.ermolenko@jetbrains.com")
            developer("nikitinas", "Anatoly Nikitin", "anatoly.nikitin@jetbrains.com")
        }
    }

    publication {
        publicationName = "api"
        artifactId = projectName
        description = "Data processing in Kotlin"
        packageName = artifactId
    }

    localRepositories {
        maven {
            // Maven directory for the `:publishLocal` task
            url = project.file(layout.buildDirectory.dir("maven")).toURI()
        }
    }
}
