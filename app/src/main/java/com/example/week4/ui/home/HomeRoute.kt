package com.example.week4.ui.home

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


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
            // HomeFeedWithArticleDetailsScreen()
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
            //ArticleScreen()
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



