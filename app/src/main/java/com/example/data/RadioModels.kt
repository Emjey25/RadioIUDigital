package com.example.data

import com.example.data.api.RadioBrowserStationDto

data class RadioStation(
    val id: String,
    val name: String,
    val frequency: String,
    val shortCategory: String,
    val subtitle: String,
    val category: StationCategory,
    val iconType: StationIconType,
    val isLive: Boolean = true,
    val streamQuality: String = "Lossless AAC • 320 kbps",
    val description: String = "",
    val streamUrl: String = "",
    val faviconUrl: String? = null,
    val country: String? = null,
    val votes: Int? = null,
    val isApiStation: Boolean = false
)

fun RadioBrowserStationDto.toRadioStation(): RadioStation {
    val cleanTags = tags?.lowercase() ?: ""
    val category = when {
        cleanTags.contains("news") || cleanTags.contains("noticias") || cleanTags.contains("talk") || cleanTags.contains("debate") -> StationCategory.NEWS
        cleanTags.contains("tech") || cleanTags.contains("pop") || cleanTags.contains("dance") || cleanTags.contains("electronic") -> StationCategory.TECH
        cleanTags.contains("youth") || cleanTags.contains("rock") || cleanTags.contains("indie") || cleanTags.contains("top40") || cleanTags.contains("urban") -> StationCategory.YOUTH
        cleanTags.contains("academic") || cleanTags.contains("cultura") || cleanTags.contains("classical") || cleanTags.contains("education") -> StationCategory.ACADEMIC
        else -> StationCategory.ALL
    }

    val stream = urlResolved?.ifBlank { null } ?: url ?: ""
    val qualityStr = if (bitrate != null && bitrate > 0) {
        val codecLabel = if (!codec.isNullOrBlank()) codec.uppercase() else "AAC"
        "$codecLabel • $bitrate kbps"
    } else if (!codec.isNullOrBlank()) {
        "${codec.uppercase()} • Stream HQ"
    } else {
        "Radio-Browser HQ • En Vivo"
    }

    val displayFreq = when {
        !country.isNullOrBlank() && !state.isNullOrBlank() -> "$state, $country • Radio-Browser API"
        !country.isNullOrBlank() -> "$country • Radio-Browser API"
        else -> "Streaming Global • Radio-Browser API"
    }

    val displayShortCat = if (!tags.isNullOrBlank()) {
        tags.split(",").take(2).joinToString(" • ") { it.trim().replaceFirstChar { c -> c.uppercase() } }
    } else {
        "Emisora En Vivo • Online"
    }

    val icon = when (category) {
        StationCategory.ACADEMIC -> StationIconType.PODCASTS
        StationCategory.TECH -> StationIconType.MEMORY
        StationCategory.NEWS -> StationIconType.FORUM
        StationCategory.YOUTH -> StationIconType.GRAPHIC_EQ
        else -> StationIconType.GRAPHIC_EQ
    }

    val validFavicon = favicon?.ifBlank { null }?.let {
        if (it.startsWith("http://") || it.startsWith("https://")) it else null
    }

    return RadioStation(
        id = stationuuid,
        name = name.trim().ifEmpty { "Emisora Sin Nombre" },
        frequency = displayFreq,
        shortCategory = displayShortCat,
        subtitle = homepage?.ifBlank { null } ?: "Transmisión en tiempo real vía Radio-Browser",
        category = category,
        iconType = icon,
        isLive = true,
        streamQuality = qualityStr,
        description = "Emisora obtenida en tiempo real de Radio-Browser API. Calidad: $qualityStr.",
        streamUrl = stream,
        faviconUrl = validFavicon,
        country = country,
        votes = votes,
        isApiStation = true
    )
}

enum class StationCategory(val label: String) {
    ALL("Todas"),
    ACADEMIC("Académica"),
    TECH("Tecnología"),
    NEWS("Debate & Noticias"),
    YOUTH("Música & Juvenil")
}

enum class StationIconType {
    PODCASTS,
    MEMORY,
    FORUM,
    GRAPHIC_EQ
}

data class PodcastProgram(
    val id: String,
    val title: String,
    val host: String,
    val duration: String,
    val date: String,
    val category: String,
    val station: String
)

data class RadioNotification(
    val id: String,
    val title: String,
    val description: String,
    val timestamp: String,
    val isUnread: Boolean = true
)
