package com.tgeanezini.mobile.codehero

data class CharacterStories (
    var available: Int,
    var collectionURI: String,
    var items: List<CharacterItems>,
    var returned: Int
)