package com.example.week4.ui.interests

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.week4.data.interests.InterestSection
import com.example.week4.data.interests.InterestsRepository
import com.example.week4.data.interests.TopicSelection
import com.example.week4.data.successOr
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class InterestsViewModel(private val interestsRepository: InterestsRepository) : ViewModel() {

    // 对应兴趣屏幕UI状态
    data class InterestsUiState(
        val topics: List<InterestSection> = emptyList(),
        val people: List<String> = emptyList(),
        val publications: List<String> = emptyList(),
        val loading: Boolean = false
    )


    //ui状态暴漏给ui界面
    private val _uiState = MutableStateFlow(InterestsUiState(loading = true))

    //tag  MutableStateFlow.asStateFlow
    val uiState: StateFlow<InterestsUiState> = _uiState.asStateFlow()

    //tag 冷流 热流
    val selectedTopics = interestsRepository.observeTopicsSelected().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet()
    )
    val selectedPeople = interestsRepository.observeTopicsSelected().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet()
    )
    val selectedPublications = interestsRepository.observeTopicsSelected().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet()
    )

    init {
        //首次进入刷新
        refreshAll()
    }

    private fun refreshAll() {
        _uiState.update { it.copy(loading = true) }
        viewModelScope.launch {
            // Trigger repository requests in parallel
            val topicsDeferred = async { interestsRepository.getTopics() }
            val peopleDeferred = async { interestsRepository.getPeople() }
            val publicationsDeferred = async { interestsRepository.getPublications() }

            // Wait for all requests to finish
            val topics = topicsDeferred.await().successOr(emptyList())
            val people = peopleDeferred.await().successOr(emptyList())
            val publications = publicationsDeferred.await().successOr(emptyList())

            _uiState.update {
                it.copy(
                    loading = false,
                    topics = topics,
                    people = people,
                    publications = publications
                )
            }
        }

    }

     fun toggleTopicSelection(topic: TopicSelection) {
        viewModelScope.launch {
            interestsRepository.toggleTopicSelection(topic)
        }
    }

     fun togglePersonSelected(person: String) {
        viewModelScope.launch {
            interestsRepository.togglePersonSelected(person = person)
        }
    }

     fun togglePublicationSelected(publication: String) {
        viewModelScope.launch {
            interestsRepository.togglePublicationSelected(publication = publication)
        }
    }


    companion object {
        fun provideFactory(interestsRepository: InterestsRepository): ViewModelProvider.Factory =

            object : ViewModelProvider.Factory {
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {

                    return InterestsViewModel(interestsRepository = interestsRepository) as T
                }

            }

    }

}