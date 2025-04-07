package org.jetbrains.kotlinx.dataframe.api

@PublishedApi
internal val skipNaN_default: Boolean = false

/**
 * Default delta degrees of freedom for the standard deviation (std).
 *
 * The default is set to `1`,
 * meaning DataFrame uses [Bessel’s correction](https://en.wikipedia.org/wiki/Bessel%27s_correction) to calculate the
 * "unbiased sample standard deviation" by default.
 * This is also the standard in languages like R.
 *
 * This is different from the "population standard deviation" (where `ddof = 0`),
 * which is used in libraries like Numpy.
 */
@PublishedApi
internal val ddof_default: Int = 1
