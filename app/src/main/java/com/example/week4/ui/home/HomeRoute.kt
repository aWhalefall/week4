package com.example.week4.ui.home

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.week4.ui.artical.ArticleScreen
import com.example.week4.ui.artical.sharePost
import com.example.week4.ui.rememberContentPaddingForScreen
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.isActive


@Composable
fun HomeRoute(
    homeViewModel: HomeViewModel,
    isExpandedScreen: Boolean,
    openDrawer: () -> Unit,
    scaffoldState: ScaffoldState = rememberScaffoldState()
) {
    //调用uiState = uiState.value
    val uiState by homeViewModel.uiState.collectAsState()

    HomeRoute(
        uiState = uiState,
        isExpandedScreen = isExpandedScreen,
        onToggleFavorite = { homeViewModel.toggleFavorite(it) },
        onSelectPost = { homeViewModel.selectArticle(it) },
        onRefreshPosts = { homeViewModel.refreshPosts() },
        onErrorDismiss = { homeViewModel.errorShow(it) },
        onInteractWithFeed = { homeViewModel.interactedWithFeed() },
        onInteractWithArticleDetails = { homeViewModel.interactedWithArticleDetails(it) },
        openDrawer = openDrawer,
        scaffoldState = scaffoldState
    )


}

/**
 * Displays the Home route.
 *
 * This composable is not coupled to any specific state management.
 *
 * @param uiState (state) the data to show on the screen
 * @param isExpandedScreen (state) whether the screen is expanded
 * @param onToggleFavorite (event) toggles favorite for a post
 * @param onSelectPost (event) indicate that a post was selected
 * @param onRefreshPosts (event) request a refresh of posts
 * @param onErrorDismiss (event) error message was shown
 * @param onInteractWithFeed (event) indicate that the feed was interacted with
 * @param onInteractWithArticleDetails (event) indicate that the article details were interacted
 * with
 * @param openDrawer (event) request opening the app drawer
 * @param scaffoldState (state) state for the [Scaffold] component on this screen
 */
@Composable
fun HomeRoute(
    uiState: HomeUiState,
    isExpandedScreen: Boolean,
    onToggleFavorite: (String) -> Unit,
    onSelectPost: (String) -> Unit,
    onRefreshPosts: () -> Unit,
    onErrorDismiss: (Long) -> Unit,
    onInteractWithFeed: () -> Unit,
    onInteractWithArticleDetails: (String) -> Unit,
    openDrawer: () -> Unit,
    scaffoldState: ScaffoldState
) {
    // Construct the lazy list states for the list and the details outside of deciding which one to
    // show. This allows the associated state to survive beyond that decision, and therefore
    // we get to preserve the scroll throughout any changes to the content.

    val homeListLazyListState = rememberLazyListState()
    val articleDetailLazyListStates = when (uiState) {
        is HomeUiState.HasPosts -> uiState.postsFeed.allPosts
        is HomeUiState.NoPosts -> emptyList()
    }.associate { post ->
        key(post.id) {
            post.id to rememberLazyListState()
        }
    }
    val homeScreenType = getHomeScreenType(isExpandedScreen, uiState)

    when (homeScreenType) {
        HomeScreenType.FeedWithArticleDetails -> {
            HomeFeedWithArticleDetailsScreen(
                uiState = uiState,
                showTopAppBar = !isExpandedScreen,
                onToggleFavorite = onToggleFavorite,
                onSelectPost = onSelectPost,
                onRefreshPosts = onRefreshPosts,
                onErrorDismiss = onErrorDismiss,
                onInteractWithFeed = onInteractWithFeed,
                onInteractWithArticleDetails = onInteractWithArticleDetails,
                openDrawer = openDrawer,
                homeListLazyListState = homeListLazyListState,
                scaffoldState = scaffoldState,
                articleDetailLazyListStates = articleDetailLazyListStates
            )
        }
        HomeScreenType.Feed -> {
            HomeFeedScreen(
                uiState = uiState,
                scaffoldState = scaffoldState,
                onErrorDismiss = onErrorDismiss,
                onRefreshPosts = onRefreshPosts,
                openDrawer = openDrawer,
                onSelectPost = onSelectPost,
                onToggleFavorite = onToggleFavorite,
                homeListLazyListState = homeListLazyListState,
                showTopAppBar = !isExpandedScreen
            )
        }
        HomeScreenType.ArticleDetails -> {
            // 由以上条件保证主屏幕类型
            check(uiState is HomeUiState.HasPosts)
            ArticleScreen(
                isExpandedScreen = isExpandedScreen,
                post = uiState.selectedPost,
                onToggleFavorite = { onToggleFavorite(uiState.selectedPost.id) },
                onBack = onInteractWithFeed,
                isFavorite = uiState.favorites.contains(uiState.selectedPost.id),
                lazyListState = articleDetailLazyListStates.getValue(uiState.selectedPost.id)
            )
        }
    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeFeedWithArticleDetailsScreen(
    uiState: HomeUiState,
    showTopAppBar: Boolean,
    onToggleFavorite: (String) -> Unit,
    onSelectPost: (String) -> Unit,
    onRefreshPosts: () -> Unit,
    onErrorDismiss: (Long) -> Unit,
    onInteractWithFeed: () -> Unit,
    onInteractWithArticleDetails: (String) -> Unit,
    openDrawer: () -> Unit,
    homeListLazyListState: LazyListState,
    articleDetailLazyListStates: Map<String, LazyListState>,
    scaffoldState: ScaffoldState,
    modifier: Modifier = Modifier,
) {

    HomeScreenWithList(
        uiState = uiState,
        showTopAppBar = showTopAppBar,
        onRefreshPosts = onRefreshPosts,
        onErrorDismiss = onErrorDismiss,
        openDrawer = openDrawer,
        homeListLazyListState = homeListLazyListState,
        scaffoldState = scaffoldState,
        modifier = modifier
    ) { hasPostsUiState, contentModifier ->

        val contentPadding = rememberContentPaddingForScreen(additionalTop = 8.dp)
        Row(contentModifier) {
            PostList(
                hasPostsUiState.postsFeed,
                hasPostsUiState.favorites,
                showExpandedSearch = !showTopAppBar,
                onArticleTapped = onSelectPost,
                onToggleFavorite = onToggleFavorite,
                contentPadding = contentPadding, modifier = Modifier
                    .width(334.dp)
                    .notifyInput(onInteractWithFeed), state = homeListLazyListState
            )
            Crossfade(targetState = hasPostsUiState.selectedPost) { detailPost ->
                // TODO: 状态的状态
                //获取此详细视图的惰性列表状态
                val detailLazyListState by derivedStateOf {
                    articleDetailLazyListStates.getValue(detailPost.id)
                }
                //针对帖子 ID 的键以避免在不同帖子之间共享任何状态
                key(detailPost.id) {

                    LazyColumn(
                        state = detailLazyListState,
                        contentPadding = contentPadding,
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .fillMaxSize()
                            .notifyInput {
                                onInteractWithArticleDetails(detailPost.id)
                            }
                    ) {
                        // TODO: 粘性头
                        stickyHeader {
                            val context = LocalContext.current
                            PostTopBar(
                                isFavorite = hasPostsUiState.favorites.contains(detailPost.id),
                                onToggleFavorite = { onToggleFavorite(detailPost.id) },
                                onSharePost = { sharePost(detailPost, context) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentWidth(Alignment.End)
                            )

                        }
                    }

                }


            }
        }

    }
}

@Composable
fun PostTopBar(
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onSharePost: () -> Unit,
    modifier: Modifier = Modifier
) {


}

// TODO: Modifier.composed 修饰符用法
private fun Modifier.notifyInput(block: () -> Unit): Modifier = composed {
    val blockState = rememberUpdatedState(block)
    pointerInput(Unit) {
        while (currentCoroutineContext().isActive) {
            awaitPointerEventScope {
                awaitPointerEvent(PointerEventPass.Initial)
                blockState.value()
            }
        }
    }
}

/**
 * A precise enumeration of which type of screen to display at the home route.
 *
 * There are 3 options:
 * - [FeedWithArticleDetails], which displays both a list of all articles and a specific article.
 * - [Feed], which displays just the list of all articles
 * - [ArticleDetails], which displays just a specific article.
 */
enum class HomeScreenType {
    FeedWithArticleDetails, Feed, ArticleDetails
}

@Composable
fun getHomeScreenType(expandedScreen: Boolean, uiState: HomeUiState): HomeScreenType =
    when (expandedScreen) {
        false -> {
            when (uiState) {
                is HomeUiState.HasPosts -> {
                    if (uiState.isArticleOpen) {
                        HomeScreenType.ArticleDetails
                    } else {
                        HomeScreenType.Feed
                    }
                }
                is HomeUiState.NoPosts -> HomeScreenType.Feed
            }
        }
        true -> HomeScreenType.FeedWithArticleDetails
    }



