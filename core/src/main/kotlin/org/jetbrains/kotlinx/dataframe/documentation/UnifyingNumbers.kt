package org.jetbrains.kotlinx.dataframe.documentation

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
 */
internal interface UnifyingNumbers {

    /**
     * ```
     *            BigDecimal
     *            /      \\
     *      BigInteger    \\
     *        /   \\        \\
     *    ULong   Long    Double
     * ..   |    /   |   /   |  \\..
     *   \\  |   /    |  /    |
     *     UInt     Int    Float
     * ..   |    /   |   /      \\..
     *   \\  |   /    |  /
     *    UShort   Short
     *      |    /   |
     *      |   /    |
     *    UByte     Byte
     * ```
     */
    interface Graph
}
