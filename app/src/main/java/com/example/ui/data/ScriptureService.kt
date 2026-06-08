package com.example.ui.data

import kotlin.random.Random

object ScriptureService {
    private val scriptures = listOf(
        Scripture(
            "Be still, and know that I am God.",
            "Psalms 46:10",
            "In the busyness of life, stillness is where we find His strength."
        ),
        Scripture(
            "For I know the plans I have for you, declares the Lord, plans for welfare and not for evil, to give you a future and a hope.",
            "Jeremiah 29:11",
            "Trust that your path is guided by a loving hand, even when you cannot see the horizon."
        ),
        Scripture(
            "The Lord is my shepherd; I shall not want.",
            "Psalms 23:1",
            "Rest in the assurance that your needs are seen and provided for."
        )
    )

    fun getRandomScripture(): Scripture {
        return scriptures.random()
    }
}
