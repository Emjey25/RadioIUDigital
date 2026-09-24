package com.example.data.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RadioBrowserStationDto(
    @Json(name = "stationuuid") val stationuuid: String,
    @Json(name = "name") val name: String,
    @Json(name = "url") val url: String? = null,
    @Json(name = "url_resolved") val urlResolved: String? = null,
    @Json(name = "homepage") val homepage: String? = null,
    @Json(name = "favicon") val favicon: String? = null,
    @Json(name = "tags") val tags: String? = null,
    @Json(name = "country") val country: String? = null,
    @Json(name = "countrycode") val countrycode: String? = null,
    @Json(name = "state") val state: String? = null,
    @Json(name = "language") val language: String? = null,
    @Json(name = "votes") val votes: Int? = null,
    @Json(name = "codec") val codec: String? = null,
    @Json(name = "bitrate") val bitrate: Int? = null,
    @Json(name = "clickcount") val clickcount: Int? = null
)
