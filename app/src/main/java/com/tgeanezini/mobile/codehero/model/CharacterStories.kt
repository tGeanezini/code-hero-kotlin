package com.tgeanezini.mobile.codehero.model

import com.tgeanezini.mobile.codehero.model.CharacterItems

data class CharacterStories (
    var available: Int,
    var collectionURI: String,
    var items: List<CharacterItems>,
    var returned: Int
)