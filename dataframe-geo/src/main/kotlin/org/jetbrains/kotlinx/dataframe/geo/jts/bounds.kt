package org.jetbrains.kotlinx.dataframe.geo.jts

import org.locationtech.jts.geom.Envelope
import org.locationtech.jts.geom.Geometry

fun Iterable<Geometry>.computeBounds(): Envelope {
    val bounds = Envelope()
    forEach { geometry -> bounds.expandToInclude(geometry.envelopeInternal) }
    return bounds
}
