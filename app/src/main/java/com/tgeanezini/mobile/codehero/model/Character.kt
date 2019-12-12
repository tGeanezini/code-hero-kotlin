package com.tgeanezini.mobile.codehero.model

data class Character (
    var id: Int,
    var name: String,
    var description: String,
    var modified: String,
    var thumbnail: CharacterThumbnail,
    var resourceUri: String,
    var comics: CharacterComics,
    var series: CharacterSeries,
    var stories: CharacterStories,
    var events: CharacterEvents,
    var urls: List<Url>
)