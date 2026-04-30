plugins {
    id("dfsettings.catalogs")
    id("dev.panuszewski.typesafe-conventions")
}

typesafeConventions {
    // prevents convention plugins being applied as `dependencies { implementation() }`
    autoPluginDependencies = false
}
