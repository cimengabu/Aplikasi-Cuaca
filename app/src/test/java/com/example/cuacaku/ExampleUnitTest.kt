package com.example.cuacaku

import com.example.cuacaku.util.resolveCurrentWeather
import com.example.cuacaku.util.resolveDailyWeather
import org.junit.Assert.assertEquals
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testCloudySkyWithZeroRainResolvesToCloudyNotDrizzle() {
        // Kode 51 biasanya gerimis di model WMO standar.
        // Tetapi dengan perbaikan kita, jika precipitation = 0.0 dan cloudCover = 80%,
        // harus menjadi Berawan (w_cloudy), BUKAN Gerimis (w_drizzle)!
        val resolved = resolveCurrentWeather(
            code = 51,
            precipitation = 0.0,
            cloudCover = 80,
            isDay = true
        )
        assertEquals(R.string.w_cloudy, resolved.labelRes)
        assertEquals("☁️", resolved.emoji)
        assertEquals(3, resolved.effectiveCode)
    }

    @Test
    fun testLowRainWithPartlyCloudyResolvesToPartlyCloudy() {
        // Kode hujan (61) tapi curah hujan 0.05 mm (< 0.2) dan tutupan awan 40%
        val resolved = resolveCurrentWeather(
            code = 61,
            precipitation = 0.05,
            cloudCover = 40,
            isDay = true
        )
        assertEquals(R.string.w_partly, resolved.labelRes)
        assertEquals("⛅", resolved.emoji)
    }

    @Test
    fun testLowRainWithClearSkyResolvesToClear() {
        // Kode gerimis (53) tapi curah hujan 0.0 mm dan tutupan awan 10%
        val resolved = resolveCurrentWeather(
            code = 53,
            precipitation = 0.0,
            cloudCover = 10,
            isDay = true
        )
        assertEquals(R.string.w_clear, resolved.labelRes)
        assertEquals("☀️", resolved.emoji)
    }

    @Test
    fun testActualDrizzleResolvesCorrectly() {
        // Curah hujan 0.5 mm (antara 0.2 - 1.0 mm) -> Gerimis
        val resolved = resolveCurrentWeather(
            code = 51,
            precipitation = 0.5,
            cloudCover = 80,
            isDay = true
        )
        assertEquals(R.string.w_drizzle, resolved.labelRes)
        assertEquals("🌦️", resolved.emoji)
    }

    @Test
    fun testActualRainResolvesCorrectly() {
        // Curah hujan 2.5 mm (> 1.0 mm) -> Hujan
        val resolved = resolveCurrentWeather(
            code = 61,
            precipitation = 2.5,
            cloudCover = 90,
            isDay = true
        )
        assertEquals(R.string.w_rain, resolved.labelRes)
        assertEquals("🌧️", resolved.emoji)
    }

    @Test
    fun testHeavyRainResolvesCorrectly() {
        // Curah hujan 10.0 mm (> 7.5 mm) -> Hujan lebat
        val resolved = resolveCurrentWeather(
            code = 65,
            precipitation = 10.0,
            cloudCover = 100,
            isDay = true
        )
        assertEquals(R.string.w_heavy_rain, resolved.labelRes)
        assertEquals("🌧️", resolved.emoji)
    }

    @Test
    fun testDailyRainFilterWhenLowProbability() {
        // Peluang hujan 20% dan total curah hujan 0.3 mm -> tidak boleh tampil hujan
        val resolved = resolveDailyWeather(
            code = 61,
            rainProb = 20,
            precipitationSum = 0.3
        )
        assertEquals(R.string.w_partly, resolved.labelRes)
        assertEquals("⛅", resolved.emoji)
    }
}
