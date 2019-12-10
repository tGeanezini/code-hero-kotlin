package com.tgeanezini.mobile.codehero

data class Character (
    var id: Int,
    var name: String,
    var description: String,
    var modified: String,
    var thumbnail: CharacterThumbnail,
    var resourceUri: String,
    var comics: List<CharacterComics>,
    var series: List<CharacterSeries>,
    var stories: List<CharacterStories>,
    var events: List<CharacterEvents>,
    var urls: List<Url>
)