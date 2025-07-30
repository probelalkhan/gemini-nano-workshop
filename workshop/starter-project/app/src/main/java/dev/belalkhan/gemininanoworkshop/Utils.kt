package dev.belalkhan.gemininanoworkshop

import kotlin.math.round

fun Long.bytesToMB(): Double {
    if (this < 0) {
        return 0.0
    }
    val bytesInOneMB = 1024.0 * 1024.0
    val megabytes = this / bytesInOneMB
    return round(megabytes * 100.0) / 100.0
}