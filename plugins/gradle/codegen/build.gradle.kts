allprojects {
    version = "1.0-SNAPSHOT"
}

buildscript {
    dependencies {
        classpath("com.vanniktech:gradle-maven-publish-plugin:0.17.0")
    }

    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
