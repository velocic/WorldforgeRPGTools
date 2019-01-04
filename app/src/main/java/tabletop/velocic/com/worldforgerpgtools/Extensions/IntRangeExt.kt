package tabletop.velocic.com.worldforgerpgtools.Extensions

import kotlin.random.Random

fun IntRange.random() : Int =
    Random.nextInt(start, endInclusive + 1)
