package com.tgeanezini.mobile.codehero.model

data class CharacterComics (
    var available: Int,
    var collectionURI: String,
    var items: List<CharacterItems>,
    var returned: Int
)