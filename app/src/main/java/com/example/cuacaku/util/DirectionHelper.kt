package com.example.cuacaku.util

import com.example.cuacaku.R

// =====================================================
// Helper untuk mengonversi derajat sudut angin ke nama mata angin
// =====================================================

object DirectionHelper {
    /** Mengembalikan string resource ID mata angin berdasarkan derajat (0-360) */
    fun getWindDirectionRes(degrees: Double): Int {
        val normalized = (degrees % 360 + 360) % 360
        return when {
            normalized in 22.5..<67.5 -> R.string.dir_ne
            normalized in 67.5..<112.5 -> R.string.dir_e
            normalized in 112.5..<157.5 -> R.string.dir_se
            normalized in 157.5..<202.5 -> R.string.dir_s
            normalized in 202.5..<247.5 -> R.string.dir_sw
            normalized in 247.5..<292.5 -> R.string.dir_w
            normalized in 292.5..<337.5 -> R.string.dir_nw
            else -> R.string.dir_n
        }
    }
}
