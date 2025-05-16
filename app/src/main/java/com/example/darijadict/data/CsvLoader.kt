package com.example.darijadict.data

import android.content.Context
import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader

object CsvLoader {
    fun loadEntriesFromAssets(context: Context): List<Entry> {
        val entries = mutableListOf<Entry>()
        try {
            val inputStream = context.assets.open("darija_dictionary.csv")
            val reader = BufferedReader(InputStreamReader(inputStream))

            reader.readLine() // Skip the header

            reader.forEachLine { line ->
                val cols = line.split(";").map { it.trim() }

                if (cols.size >= 13) {
                    val entry = Entry(
                        word = cols[0],
                        meaning = cols[1],
                        pos = cols[2].takeIf { it.isNotEmpty() },
                        plural = cols[3].takeIf { it.isNotEmpty() },
                        present = cols[4].takeIf { it.isNotEmpty() },
                        fs = cols[5].takeIf { it.isNotEmpty() },
                        mp = cols[6].takeIf { it.isNotEmpty() },
                        fp = cols[7].takeIf { it.isNotEmpty() },
                        uses = cols[8].takeIf { it.isNotEmpty() },
                        example = cols[9].takeIf { it.isNotEmpty() },
                        arabicScript = cols[10],
                        pronunciation = cols[11].removePrefix("[sound:").removeSuffix("]").takeIf { it.isNotEmpty() },
                        usesPronunciation = cols[12].removePrefix("[sound:").removeSuffix("]").takeIf { it.isNotEmpty() }
                    )
                    entries.add(entry)
                }
            }
            reader.close()
        } catch (e: Exception) {
            Log.e("CsvLoader", "Error loading entries", e)
        }
        return entries
    }

    fun loadGrammarLessonsFromAssets(context: Context): List<GrammarLesson> {
        val lessons = mutableListOf<GrammarLesson>()
        try {
            val inputStream = context.assets.open("grammar_lessons.csv")
            val reader = BufferedReader(InputStreamReader(inputStream))

            reader.readLine() // Skip header

            reader.forEachLine { line ->
                val cols = line.split(";").map { it.trim() }
                if (cols.size >= 2) {
                    lessons.add(
                        GrammarLesson(
                            lesson = cols[0],
                            link = cols[1]
                        )
                    )
                }
            }
            reader.close()
        } catch (e: Exception) {
            Log.e("CsvLoader", "Error loading grammar lessons", e)
        }
        return lessons
    }

    fun loadRecommendationsFromAssets(context: Context): List<Recommendation> {
        val recommendations = mutableListOf<Recommendation>()
        try {
            val inputStream = context.assets.open("recommendations.csv")
            val reader = BufferedReader(InputStreamReader(inputStream))
            reader.readLine() //skip header
            reader.forEachLine { line ->
                val cols = line.split(";").map { it.trim() }
                if (cols.size >= 2) {
                    recommendations.add(
                        Recommendation(
                            title = cols[0],
                            link = cols[1]
                        )
                    )
                }
            }
            reader.close()
        } catch (e: Exception) {
            Log.e("CsvLoader", "Error loading recommendations", e)
        }
        return recommendations
    }
}