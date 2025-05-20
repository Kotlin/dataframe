package org.jetbrains.kotlinx.dataframe.documentation

import org.jetbrains.kotlinx.dataframe.impl.UnifiedNumberTypeOptions

/**
 * ## Unifying Numbers
 *
 * The concept of unifying numbers is converting them to a common number type without losing information.
 *
 * The following graph shows the hierarchy of number types in Kotlin DataFrame.
 * The order is top-down from the most complex type to the simplest one.
 *
 * {@include [Graph]}
 * For each number type in the graph, it holds that a number of that type can be expressed lossless by
 * a number of a more complex type (any of its parents).
 * This is either because the more complex type has a larger range or higher precision (in terms of bits).
 *
 * There are variants of this graph that exclude some types, such as `BigDecimal` and `BigInteger`.
 * In these cases `Double` could be considered the most complex type.
 * `Long`/`ULong` and `Double` could be joined to `Double`,
 * potentially losing a little precision, but a warning will be given.
 *
 * See [UnifiedNumberTypeOptions] for these settings.
 *
 * Nullability, while not displayed in the graph, is also taken into account.
 * This means that `Int?` and `Float` will be unified to `Double?`.
 *
 * At the bottom of the graph is [Nothing].
 * This can be interpreted as "no type" and can have no instance,
 * while [Nothing?][Nothing] can only be `null`.
 */
public interface UnifyingNumbers {

    /**
     * ```
     *           (BigDecimal)
     *            /      \\
     *     (BigInteger)   \\
     *        /   \\        \\
     * <~ ULong   Long ~> Double ..
     * ..   |    /   |   /   |  \\..
     *   \\  |   /    |  /    |
     *     UInt     Int    Float
     * ..   |    /   |   /      \\..
     *   \\  |   /    |  /
     *    UShort   Short
     *      |    /   |
     *      |   /    |
     *    UByte     Byte
     *        \\     /
     *        \\    /
     *       Nothing
     * ```
     */
    @ExcludeFromSources
    @ExportAsHtml
    private interface Graph
}
