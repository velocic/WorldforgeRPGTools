package tabletop.velocic.com.worldforgerpgtools.extensions

fun IntRange.Companion.combineAscending(first: IntRange, second: IntRange): IntRange {
    val newMin = minOf(minOf(first.first, first.last), second.first, second.last)
    val newMax = maxOf(maxOf(first.first, first.last), second.first, second.last)

    return newMin..newMax
}