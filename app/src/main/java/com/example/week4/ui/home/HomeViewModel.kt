package com.example.week4.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.jetnews.R
import com.example.week4.data.Response
import com.example.week4.data.posts.PostsRepository
import com.example.week4.model.Post
import com.example.week4.model.PostsFeed
import com.example.week4.utils.ErrorMessage
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * author：yangweichao@reworldgame.com
 * data: 2022/4/2 16:39
 * 密封接口,数据类主构造函数中复写属性
 */
sealed interface HomeUiState {
    val isLoading: Boolean
    val errorMessages: List<ErrorMessage>

    data class NoPosts(
        override val isLoading: Boolean,
        override val errorMessages: List<ErrorMessage>
    ) : HomeUiState

    data class HasPosts(
        val postsFeed: PostsFeed,
        val selectedPost: Post,
        val isArticleOpen: Boolean,
        val favorites: Set<String>,
        override val isLoading: Boolean,
        override val errorMessages: List<ErrorMessage>
    ) : HomeUiState
}

/**
 * An internal  representationof the Home route state, in a raw form
 * Home 路由状态的内部表示，以原始形式
 */
private data class HomeViewModelState(
    val postsFeed: PostsFeed? = null,
    val selectedPostId: String? = null, // TODO back selectedPostId in a SavedStateHandle
    val isArticleOpen: Boolean = false,
    val favorites: Set<String> = emptySet(),
    val isLoading: Boolean = false,
    val errorMessages: List<ErrorMessage> = emptyList(),
) {
    fun toUiState(): HomeUiState = if (postsFeed == null) {
        HomeUiState.NoPosts(isLoading = isLoading, errorMessages = errorMessages)
    } else {
        HomeUiState.HasPosts(postsFeed = postsFeed,
            //确定所选帖子。这将是用户最后选择的帖子。如果没有（或该帖子不在当前提要中），则默认为突出显示的帖子
            selectedPost = postsFeed.allPosts.find { it.id == selectedPostId }
                ?: postsFeed.highlightedPost,
            isArticleOpen = isArticleOpen,
            favorites = favorites,
            isLoading = isLoading,
            errorMessages = errorMessages
        )

    }

}

class HomeViewModel(private val postsRepository: PostsRepository) : ViewModel() {

    private val viewModelState = MutableStateFlow(HomeViewModelState(isLoading = true))

    //暴露给 UI 的 UI 状态 ,共享协程，订阅者使用同一个连接
    val uiState = viewModelState.map { it.toUiState() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, viewModelState.value.toUiState())

    init {
        refreshPosts()
        //ViewModel 属性扩展 ? 协程作用域  CloseableCoroutineScope
        viewModelScope.launch {
            postsRepository.observeFavorites().collect { favorites ->
                viewModelState.update { it.copy(favorites = favorites) }
            }
        }
    }

     fun refreshPosts() {
        viewModelState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val result = postsRepository.getPostsFeed()
            viewModelState.update {
                when (result) {
                    is Response.Success -> it.copy(postsFeed = result.data, isLoading = false)
                    is Response.Error -> {
                        val errorMessages = it.errorMessages + ErrorMessage(
                            id = UUID.randomUUID().mostSignificantBits,
                            messageId = R.string.load_error
                        )
                        it.copy(errorMessages = errorMessages, isLoading = false)
                    }
                }
            }
        }
    }


    fun toggleFavorite(postId: String) {
        viewModelScope.launch {
            postsRepository.toggleFavorite(postId = postId)
        }
    }

    //选择给定的文章以查看有关它的更多信息
    fun selectArticle(postId: String) {
        // Treat selecting a detail as simply interacting with it
        interactedWithArticleDetails(postId)
    }

    fun errorShow(errorId: Long) {
        viewModelState.update { currentUiState ->
            val errorMessage = currentUiState.errorMessages.filterNot { it.id == errorId }
            currentUiState.copy(errorMessages = errorMessage)
        }
    }

     fun interactedWithArticleDetails(postId: String) {
        viewModelState.update {
            it.copy(selectedPostId = postId, isArticleOpen = true)
        }
    }

    fun interactedWithFeed() {
        viewModelState.update {
            it.copy(isArticleOpen = false)
        }
    }

    companion object {
        fun provideFactory(postsRepository: PostsRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {

                    return HomeViewModel(postsRepository = postsRepository) as T
                }

            }

    }

}

