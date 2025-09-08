package org.jetbrains.kotlinx.dataframe.annotations

/**
 * Marks API used by Kotlin DataFrame IntelliJ Plugin.
 *
 * Such API should remain stable:
 * - It should not be moved, renamed, or removed.
 * - The number of parameters and their types should not change.
 *
 * If changes to such API are required, they should first be supported in the Kotlin DataFrame IntelliJ Plugin.
 */
internal annotation class IntellijPluginApi
