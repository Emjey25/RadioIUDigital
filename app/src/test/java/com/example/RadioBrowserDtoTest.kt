package com.example

import com.example.data.api.RadioBrowserStationDto
import com.example.data.toRadioStation
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class RadioBrowserDtoTest {

    @Test
    fun dtoToRadioStation_mapsFieldsCorrectly() {
        val dto = RadioBrowserStationDto(
            stationuuid = "test-uuid-123",
            name = "Radio Colombia Live",
            url = "http://stream.radiocolombia.co/live",
            urlResolved = "http://stream.radiocolombia.co/live",
            homepage = "https://radiocolombia.co",
            favicon = "https://radiocolombia.co/icon.png",
            tags = "news,noticias,colombia",
            country = "Colombia",
            codec = "MP3",
            bitrate = 128,
            votes = 450
        )

        val station = dto.toRadioStation()

        assertEquals("test-uuid-123", station.id)
        assertEquals("Radio Colombia Live", station.name)
        assertEquals("http://stream.radiocolombia.co/live", station.streamUrl)
        assertEquals("https://radiocolombia.co/icon.png", station.faviconUrl)
        assertEquals("Colombia", station.country)
        assertTrue(station.isApiStation)
        assertEquals(450, station.votes)
        assertNotNull(station.category)
    }
}
