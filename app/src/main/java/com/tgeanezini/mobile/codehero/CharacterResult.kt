package com.tgeanezini.mobile.codehero

data class CharacterResult (
    var offset: Int,
    var limit: Int,
    var total: Int,
    var count: Int,
    var results: List<Character>
)