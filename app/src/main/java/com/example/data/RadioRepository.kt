package com.example.data

import com.example.data.api.RadioBrowserApiClient

object RadioRepository {

    suspend fun searchStations(
        query: String = "",
        country: String = "",
        limit: Int = 30
    ): Result<List<RadioStation>> {
        return try {
            val dtos = try {
                RadioBrowserApiClient.service.searchStations(
                    name = query.ifBlank { null },
                    country = country.ifBlank { null },
                    limit = limit
                )
            } catch (e: Exception) {
                RadioBrowserApiClient.fallbackService.searchStations(
                    name = query.ifBlank { null },
                    country = country.ifBlank { null },
                    limit = limit
                )
            }
            Result.success(dtos.map { it.toRadioStation() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchTopStations(limit: Int = 30): Result<List<RadioStation>> {
        return try {
            val dtos = try {
                RadioBrowserApiClient.service.getTopStations(limit = limit)
            } catch (e: Exception) {
                RadioBrowserApiClient.fallbackService.getTopStations(limit = limit)
            }
            Result.success(dtos.map { it.toRadioStation() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    val initialStations: List<RadioStation> = listOf(
        RadioStation(
            id = "iu_stereo",
            name = "IU Stereo Principal",
            frequency = "104.5 FM Digital",
            shortCategory = "Música & Cultura Universitaria • 104.5 FM",
            subtitle = "Académica & Universitaria • Transmisión HQ",
            category = StationCategory.ACADEMIC,
            iconType = StationIconType.PODCASTS,
            isLive = true,
            streamQuality = "Lossless AAC • 320 kbps",
            description = "Señal matriz universitaria con programación cultural, académica y música seleccionada para toda la comunidad de Antioquia."
        ),
        RadioStation(
            id = "facultad_tec",
            name = "Radio Facultad Tecnología",
            frequency = "Online • Canal 2",
            shortCategory = "Innovación, Podcasts & Tech • Online",
            subtitle = "Innovación, Podcasts & Tech • Transmisión HQ",
            category = StationCategory.TECH,
            iconType = StationIconType.MEMORY,
            isLive = true,
            streamQuality = "HQ AAC • 256 kbps",
            description = "Espacio dedicado a desarrollo de software, inteligencia artificial, robótica y ciencia aplicada universitaria."
        ),
        RadioStation(
            id = "voces_iu",
            name = "Voces IU Debate & Noticias",
            frequency = "98.2 FM • Central",
            shortCategory = "Actualidad Académica & Entrevistas",
            subtitle = "Actualidad Académica, Política & Entrevistas",
            category = StationCategory.NEWS,
            iconType = StationIconType.FORUM,
            isLive = true,
            streamQuality = "HD Voice • 192 kbps",
            description = "Periodismo universitario, mesas de análisis institucional, entrevistas con rectores y líderes del sector educativo."
        ),
        RadioStation(
            id = "frecuencia_joven",
            name = "IU Frecuencia Joven",
            frequency = "Streaming • Estudiantes",
            shortCategory = "Tendencias, Éxitos & Vida Estudiantil",
            subtitle = "Tendencias, Éxitos & Vida Universitaria",
            category = StationCategory.YOUTH,
            iconType = StationIconType.GRAPHIC_EQ,
            isLive = true,
            streamQuality = "Ultra Bass AAC • 320 kbps",
            description = "Música indie, urbana, rock universitario y los programas producidos 100% por los colectivos de estudiantes."
        )
    )

    val samplePodcasts: List<PodcastProgram> = listOf(
        PodcastProgram(
            id = "pod_1",
            title = "Código & Sociedad: El impacto de la IA en la educación",
            host = "Facultad de Ingeniería",
            duration = "34 min",
            date = "Hoy, 10:00 AM",
            category = "Tecnología",
            station = "Radio Facultad Tecnología"
        ),
        PodcastProgram(
            id = "pod_2",
            title = "Crónicas de Campus: Historias de emprendimiento",
            host = "Colectivo de Comunicaciones",
            duration = "45 min",
            date = "Ayer",
            category = "Vida Universitaria",
            station = "IU Frecuencia Joven"
        ),
        PodcastProgram(
            id = "pod_3",
            title = "Mesa Redonda: Presupuesto y Conectividad Rural",
            host = "Dr. Carlos Mesa & Invitados",
            duration = "52 min",
            date = "9 Sep",
            category = "Debate",
            station = "Voces IU Debate & Noticias"
        ),
        PodcastProgram(
            id = "pod_4",
            title = "Sinfonía Digital: Clásicos y sonidos experimentales",
            host = "Maestra Elena Restrepo",
            duration = "58 min",
            date = "8 Sep",
            category = "Música",
            station = "IU Stereo Principal"
        )
    )

    val sampleNotifications: List<RadioNotification> = listOf(
        RadioNotification(
            id = "notif_1",
            title = "¡Transmisión Especial En Vivo!",
            description = "Inicia el Conversatorio de Innovación 2025 por Radio Facultad Tecnología.",
            timestamp = "Hace 15 min",
            isUnread = true
        ),
        RadioNotification(
            id = "notif_2",
            title = "Nuevo episodio disponible",
            description = "Código & Sociedad: Temporada 3, Episodio 4 ya disponible en el catálogo.",
            timestamp = "Hace 2 horas",
            isUnread = true
        ),
        RadioNotification(
            id = "notif_3",
            title = "Mantenimiento de antena finalizado",
            description = "La señal 104.5 FM ahora cuenta con mayor potencia y cobertura digital.",
            timestamp = "Ayer",
            isUnread = false
        )
    )

    const val LOGO_URL = "https://lh3.googleusercontent.com/aida/AEtjO1U8WZ5TDe5YI6PZfdOsLiLZEmz1vMSelcsHwV1ukoZKCJ9jVWDWP8FdYxcikBTmtj1EbxW6DJEqy7IOttbE_u4VNiUyO-xPoP_-Z0T1DnFhcXTwKfRAHGS-zk-mq0f0P7n4tBNqoq_4Ou06O_khD0YtLPU2EEgO4B7tvwrCl68huqz7smj5cRRg7pwTYAONmKk3vSZ-lNMa-aKR7TQugBRMTwGyDXpHmBFY0sWazeETNrWEAUb36STj0bo"
    const val AVATAR_URL = "https://lh3.googleusercontent.com/aida-public/AB6AXuAG9F1suvNdoujWBXeaU-w75fxEMEvOiqWYkHy8RUFg6CRPw2rrPIwDEeYITqLLUTBoHP_BVOGhdr0uYle16oXIUfc1K-vWYsKJfLCHEMDoAXE8pD37qoxQb-unGe7zI4LqJ77AbwoW2jwC7NvZijUkbYAVwNZAyx5xNQ-_L0ol3to4YHTxCLuWX1h0758eRUMsoijA5K4xj83ET8bOXOX6GKIBtnlG6XRiSvzTdoiM-xB8EdGwzK_u"
}
