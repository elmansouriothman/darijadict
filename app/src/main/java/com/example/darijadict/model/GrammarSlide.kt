package com.example.darijadict.model

data class GrammarSlide(
    val text: String,
    val type: String,
    val order: Int,
    val arabicScript: String? = null,
    val characterEquivalent: String? = null,
    val needsAudio: Boolean = false,
    val audioFile: String? = null
)
