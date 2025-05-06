package org.jetbrains.kotlinx.dataframe.annotations

/**
 * Functions that can be replaced with other API, such as shortcuts without clean value,
 * or something not properly designed, and so will be considered to be removed from API.
 * If you see a function marked with it and think it should be kept, please let us know in the GitHub issue:
 * https://github.com/Kotlin/dataframe/issues/1028
 */
internal annotation class CandidateForRemoval
