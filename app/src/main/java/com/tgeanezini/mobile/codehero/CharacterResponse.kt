package com.tgeanezini.mobile.codehero

data class CharacterResponse (
    var code: Int,
    var status: String,
    var copyright: String,
    var attributionText: String,
    var attributionHTML: String,
    var etag: String,
    var data: CharacterResult
)