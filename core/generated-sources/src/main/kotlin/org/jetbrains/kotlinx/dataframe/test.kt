package org.jetbrains.kotlinx.dataframe

import java.awt.Panel
import java.awt.Rectangle
import java.awt.Shape
import java.awt.geom.AffineTransform
import java.awt.geom.PathIterator
import java.awt.geom.Point2D
import java.awt.geom.Rectangle2D

@DslMarker
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE, AnnotationTarget.FUNCTION)
public annotation class ShapeDsl

public class Square(public val lines: Int, public val char: Char) : Shape {
    override fun getBounds(): Rectangle {
        TODO("Not yet implemented")
    }

    override fun getBounds2D(): Rectangle2D {
        TODO("Not yet implemented")
    }

    override fun contains(x: Double, y: Double): Boolean {
        TODO("Not yet implemented")
    }

    override fun contains(p: Point2D?): Boolean {
        TODO("Not yet implemented")
    }

    override fun contains(x: Double, y: Double, w: Double, h: Double): Boolean {
        TODO("Not yet implemented")
    }

    override fun contains(r: Rectangle2D?): Boolean {
        TODO("Not yet implemented")
    }

    override fun intersects(x: Double, y: Double, w: Double, h: Double): Boolean {
        TODO("Not yet implemented")
    }

    override fun intersects(r: Rectangle2D?): Boolean {
        TODO("Not yet implemented")
    }

    override fun getPathIterator(at: AffineTransform?): PathIterator {
        TODO("Not yet implemented")
    }

    override fun getPathIterator(at: AffineTransform?, flatness: Double): PathIterator {
        TODO("Not yet implemented")
    }
}

public class Triangle(public val lines: Int, public val char: Char) : Shape {
    override fun getBounds(): Rectangle {
        TODO("Not yet implemented")
    }

    override fun getBounds2D(): Rectangle2D {
        TODO("Not yet implemented")
    }

    override fun contains(x: Double, y: Double): Boolean {
        TODO("Not yet implemented")
    }

    override fun contains(p: Point2D?): Boolean {
        TODO("Not yet implemented")
    }

    override fun contains(x: Double, y: Double, w: Double, h: Double): Boolean {
        TODO("Not yet implemented")
    }

    override fun contains(r: Rectangle2D?): Boolean {
        TODO("Not yet implemented")
    }

    override fun intersects(x: Double, y: Double, w: Double, h: Double): Boolean {
        TODO("Not yet implemented")
    }

    override fun intersects(r: Rectangle2D?): Boolean {
        TODO("Not yet implemented")
    }

    override fun getPathIterator(at: AffineTransform?): PathIterator {
        TODO("Not yet implemented")
    }

    override fun getPathIterator(at: AffineTransform?, flatness: Double): PathIterator {
        TODO("Not yet implemented")
    }
}

@ShapeDsl
public interface SquareFunction {
    //    @ShapeDsl
    public fun PanelDsl.square(init: (SquareBuilder).() -> Unit): Square = TODO()
}

@ShapeDsl
public interface TriangleFunction {
    public fun PanelDsl.triangle(init: (TriangleBuilder).() -> Unit): Triangle = TODO()
}

@ShapeDsl
public interface PanelDsl : SquareFunction, TriangleFunction {
    public val panel: Panel

    public companion object {
        public operator fun invoke(panel: Panel): PanelDsl =
            object : PanelDsl {
                override val panel: Panel
                    get() = panel
            }
    }
}

//@ShapeDsl
public inline fun panel(init: (@ShapeDsl PanelDsl).() -> Unit): Panel =
    PanelDsl(Panel()).apply(init).panel


@ShapeDsl
public abstract class ShapeBuilder {

    private companion object {
        const val DEFAULT_CHAR = '*'
    }

    public var char: Char = DEFAULT_CHAR
    public var lines: Int = 0

    public abstract fun build(): Shape
}

@ShapeDsl
public class SquareBuilder(override val panel: Panel) : ShapeBuilder(), SquareFunction, PanelDsl {
    override fun build(): Square = Square(lines, char)
}

@ShapeDsl
public class TriangleBuilder : ShapeBuilder() {
    override fun build(): Square = Square(lines, char)
}

public fun main() {
    panel {
        triangle {

        }
        square {
            lines = 8
            char = 'd'

            square {
                lines = 5
                char = 'o'

                triangle {
                    lines = 3
                    char = 'x'

//                    triangle {
//
//                    }
//                    square {
//
//                    }
                }
            }
        }
    }
}
