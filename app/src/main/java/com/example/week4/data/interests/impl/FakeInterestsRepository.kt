package com.example.week4.data.interests.impl

import com.example.week4.data.interests.InterestSection
import com.example.week4.data.interests.InterestsRepository
import com.example.week4.data.interests.TopicSelection
import kotlinx.coroutines.flow.Flow

/**
 * author：yangweichao@reworldgame.com
 * data: 2022/3/31 19:10
 * 交互资源
 */
class FakeInterestsRepository : InterestsRepository {
    private val topics by lazy {
        listOf(
            InterestSection("Android", listOf("Jetpack Compose", "Kotlin", "Jetpack")),
            InterestSection(
                "Programming",
                listOf("Kotlin", "Declarative UIs", "Java", "Unidirectional Data Flow", "C++")
            ),
            InterestSection("Technology", listOf("Pixel", "Google"))
        )
    }
    private val people by lazy {
        listOf(
            "Kobalt Toral",
            "K'Kola Uvarek",
            "Kris Vriloc",
            "Grala Valdyr",
            "Kruel Valaxar",
            "L'Elij Venonn",
            "Kraag Solazarn",
            "Tava Targesh",
            "Kemarrin Muuda"
        )
    }

    private val publications by lazy {
        listOf(
            "Kotlin Vibe",
            "Compose Mix",
            "Compose Breakdown",
            "Android Pursue",
            "Kotlin Watchman",
            "Jetpack Ark",
            "Composeshack",
            "Jetpack Point",
            "Compose Tribune"
        )
    }

    override suspend fun getTopics(): Result<List<InterestSection>> {
        TODO("Not yet implemented")
    }

    override suspend fun getPeople(): Result<List<String>> {
        TODO("Not yet implemented")
    }

    override suspend fun getPublications(): Result<List<String>> {
        TODO("Not yet implemented")
    }

    override suspend fun toggleTopicSelection(topic: TopicSelection) {
        TODO("Not yet implemented")
    }

    override suspend fun togglePersonSelected(person: String) {
        TODO("Not yet implemented")
    }

    override suspend fun togglePublicationSelected(publication: String) {
        TODO("Not yet implemented")
    }

    override fun observeTopicsSelected(): Flow<Set<TopicSelection>> {
        TODO("Not yet implemented")
    }

    override fun observePeopleSelected(): Flow<Set<String>> {
        TODO("Not yet implemented")
    }

    override fun observePublicationSelected(): Flow<Set<String>> {
        TODO("Not yet implemented")
    }

}