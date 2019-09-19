package tabletop.velocic.com.worldforgerpgtools.extensions

import kotlin.math.sqrt

operator fun <T : Number> List<T>.minus(other: List<T>) : List<T> {
    if (size != other.size) {
        throw IllegalArgumentException("Cannot subtract vectors of different dimensions.")
    }

    if (isEmpty()) {
        return listOf()
    }

    val result = mutableListOf<T>()

    for (i in indices) {
        when(this[i]) {
            is Int -> result.add((this[i] as Int - other[i] as Int) as T)
            is Double -> result.add((this[i] as Double - other[i] as Double) as T)
            is Float -> result.add((this[i] as Float - other[i] as Float) as T)
        }
    }

    return result
}

fun <T : Number> List<T>.magnitude() : Double {
    val squaresSummed = map {
        when(it) {
            is Int -> (it * it) as T
            is Double -> (it * it) as T
            is Float -> (it * it) as T
            else -> 0
        }
    }.reduce { acc, number ->
        when (number) {
            is Int -> (acc as Int + number) as T
            is Double -> (acc as Double + number) as T
            is Float -> (acc as Float + number) as T
            else -> 0
        }
    }

    return when(squaresSummed) {
        is Int -> sqrt(squaresSummed.toDouble())
        is Double -> sqrt(squaresSummed)
        is Float -> sqrt(squaresSummed.toDouble())
        else -> 0.0
    }
}

fun <T : Number> List<T>.normalize() : List<Double> =
    map {
        val magnitude = magnitude()
        when (it) {
            is Int -> it.toDouble() / magnitude
            is Double -> it / magnitude
            is Float -> it.toDouble() / magnitude
            else -> 0.0
        }
    }
