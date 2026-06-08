package org.jetbrains.kotlinx.dataframe.example.compose

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.getColumnsWithPaths
import org.jetbrains.kotlinx.dataframe.api.isColumnGroup
import org.jetbrains.kotlinx.dataframe.api.rows
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.size

private val BorderColor = Color(0xFFBDBDBD)
private val HeaderBg = Color(0xFFE3F2FD)
private val EvenRowBg = Color(0xFFFFFFFF)
private val OddRowBg = Color(0xFFF5F5F5)
private val TooltipBg = Color(0xFFFFFFE0)

private val DefaultCellWidth: Dp = 160.dp
private val FrameCellWidth: Dp = 480.dp

/**
 * Width allocated to a single leaf column. For [FrameColumn]s, recurse into the inner frames
 * so the column ends up wide enough to fit the widest expanded nested table — that way the
 * outer header still lines up with the body when the user expands a frame.
 */
private fun leafWidth(col: AnyCol): Dp =
    when (col) {
        is FrameColumn<*> -> {
            val innerWidths: List<Dp> = col.toList().mapNotNull { inner ->
                (inner as? AnyFrame)?.totalLeafWidth()
            }
            (innerWidths.maxOrNull() ?: 0.dp).coerceAtLeast(FrameCellWidth)
        }

        else -> DefaultCellWidth
    }

/** Total width of all leaves in this frame, summed in body-order. */
private fun AnyFrame.totalLeafWidth(): Dp {
    val leaves = getColumnsWithPaths { colsAtAnyDepth().filter { !it.isColumnGroup() } }
    return leaves.fold(0.dp) { acc, l -> acc + leafWidth(l.data) }
}

/** Depth of the deepest [ColumnGroup] nesting, starting at 0. */
private fun AnyFrame.headerDepth(startingAt: Int = 0): Int =
    columns().maxOfOrNull {
        if (it is ColumnGroup<*>) it.headerDepth(startingAt + 1) else startingAt
    } ?: startingAt

/** Number of leaf-column slots this column occupies in the flat body layout. */
private fun AnyCol.leafSpan(): Int =
    if (this is ColumnGroup<*>) columns().sumOf { it.leafSpan() }.coerceAtLeast(1) else 1

/** A cell in the header grid: either a column header or empty filler, spanning N leaf slots. */
private data class HeaderEntry(val column: AnyCol?, val span: Int)

/**
 * Build the header row at the given nesting [depth]. A column-group at [depth] emits one entry
 * spanning all its leaves; a leaf column at a depth shallower than [depth] emits a 1-wide filler
 * (so the grid keeps lining up with the body).
 */
private fun headerRowAt(df: AnyFrame, depth: Int): List<HeaderEntry> {
    val result = mutableListOf<HeaderEntry>()
    fun visit(col: AnyCol, currentDepth: Int) {
        when {
            currentDepth == depth -> result += HeaderEntry(col, col.leafSpan())
            col is ColumnGroup<*> -> col.columns().forEach { visit(it, currentDepth + 1) }
            else -> result += HeaderEntry(null, 1)
        }
    }
    df.columns().forEach { visit(it, 0) }
    return result
}

private fun Modifier.cell(background: Color, width: Dp): Modifier =
    this
        .width(width)
        .background(background)
        .border(width = 1.dp, color = BorderColor)
        .padding(horizontal = 8.dp, vertical = 4.dp)

/**
 * Scrollable [DataFrame] viewer. The caller should give [modifier] a bounded size
 * (e.g. `Modifier.fillMaxSize()`) so the internal scroll has a viewport.
 */
@Composable
fun <T> DataFrameTable(df: DataFrame<T>, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .horizontalScroll(rememberScrollState()),
    ) {
        DataFrameTableContent(df)
    }
}

@Composable
private fun <T> DataFrameTableContent(df: DataFrame<T>) {
    // Flat list of leaf (non-group) columns — body rows iterate exactly these, in order.
    val leaves: List<ColumnWithPath<*>> = df.getColumnsWithPaths {
        colsAtAnyDepth().filter { !it.isColumnGroup() }
    }
    val leafWidths: List<Dp> = leaves.map { leafWidth(it.data) }
    val maxDepth = df.headerDepth()

    Column(modifier = Modifier.border(width = 1.dp, color = BorderColor)) {
        // Header grid: one Row per nesting depth.
        for (depth in 0..maxDepth) {
            val row = headerRowAt(df, depth)
            Row {
                var leafIndex = 0
                row.forEach { entry ->
                    val width = (leafIndex until leafIndex + entry.span)
                        .fold(0.dp) { acc, i -> acc + leafWidths[i] }
                    HeaderCell(entry.column, width)
                    leafIndex += entry.span
                }
            }
        }

        // Body: one Row per data row, one Cell per leaf column.
        df.rows().forEachIndexed { index, row ->
            val rowBg = if (index % 2 == 0) EvenRowBg else OddRowBg
            Row {
                leaves.forEachIndexed { i, col ->
                    DataCell(row[col.path()], col.data, leafWidths[i], rowBg)
                }
            }
        }
    }
}

@Composable
private fun HeaderCell(column: AnyCol?, width: Dp) {
    if (column == null) {
        // Filler so the grid stays aligned underneath shallower leaf columns.
        Box(modifier = Modifier.width(width).background(HeaderBg))
    } else {
        Box(modifier = Modifier.cell(HeaderBg, width)) {
            TruncatedText(column.name(), fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun DataCell(value: Any?, col: AnyCol, width: Dp, rowBg: Color) {
    Box(modifier = Modifier.cell(rowBg, width)) {
        when (col) {
            is FrameColumn<*> -> ExpandableFrame(value as AnyFrame)
            else -> TruncatedText(value?.toString() ?: "null")
        }
    }
}

@Composable
private fun ExpandableFrame(frame: AnyFrame) {
    var expanded by remember { mutableStateOf(false) }
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = if (expanded) "▼ " else "▶ ", fontWeight = FontWeight.Bold)
            Text(text = "DataFrame ${frame.size()}")
        }
        if (expanded) {
            // Recursive call — use the inner Content (no scroll wrapper) so we don't
            // nest scroll containers, which would error on infinite constraints.
            DataFrameTableContent(frame)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TruncatedText(text: String, fontWeight: FontWeight? = null) {
    TooltipArea(
        tooltip = {
            Surface(
                shadowElevation = 4.dp,
                color = TooltipBg,
                border = BorderStroke(1.dp, BorderColor),
            ) {
                Text(
                    text = text,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                )
            }
        },
    ) {
        Text(
            text = text,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontWeight = fontWeight,
        )
    }
}
