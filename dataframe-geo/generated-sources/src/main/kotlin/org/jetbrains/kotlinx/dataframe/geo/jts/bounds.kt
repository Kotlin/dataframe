package org.jetbrains.kotlinx.dataframe.geo.jts

import org.locationtech.jts.geom.Envelope
import org.locationtech.jts.geom.Geometry

/**
 * Computes the bounding envelope for a collection of geometries.
 *
 *
 * @receiver The collection of geometries for which to compute the bounds.
 * @return The minimal envelope that encompasses all geometries in the collection.
 */
public fun Iterable<Geometry>.computeBounds(): Envelope {
    val bounds = Envelope()
    forEach { geometry -> bounds.expandToInclude(geometry.envelopeInternal) }
    return bounds
}
