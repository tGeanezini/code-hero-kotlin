package com.tgeanezini.mobile.codehero.model

import com.tgeanezini.mobile.codehero.model.Character

data class CharacterResult (
    var offset: Int,
    var limit: Int,
    var total: Int,
    var count: Int,
    var results: List<Character>
)