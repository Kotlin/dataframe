import dev.panuszewski.gradle.TypesafeConventionsExtension

plugins {
    id("dfsettings.catalogs")
    id("dev.panuszewski.typesafe-conventions")
}

extensions.getByType<TypesafeConventionsExtension>().apply {
    // prevents convention plugins being applied as `dependencies { implementation() }`
    autoPluginDependencies = false
}
