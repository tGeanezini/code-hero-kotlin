package com.tgeanezini.mobile.codehero

data class CharacterEvents (
    var available: Int,
    var collectionURI: String,
    var items: List<CharacterItems>,
    var returned: Int
)