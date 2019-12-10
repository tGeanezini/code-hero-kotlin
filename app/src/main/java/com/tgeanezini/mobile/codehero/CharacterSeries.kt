package com.tgeanezini.mobile.codehero

data class CharacterSeries (
    var available: Int,
    var collectionURI: String,
    var items: List<CharacterItems>,
    var returned: Int
)