package com.example.week4.ui.home

import android.content.res.Configuration
import androidx.collection.SparseArrayCompat
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.jetnews.R
import com.example.week4.data.Response
import com.example.week4.data.interests.impl.posts
import com.example.week4.data.posts.impl.BlockingFakePostsRepository
import com.example.week4.model.Post
import com.example.week4.model.PostsFeed
import com.example.week4.ui.components.JetnewsSnackbarHost
import com.example.week4.ui.rememberContentPaddingForScreen
import com.example.week4.ui.theme.Week4Theme
import com.example.week4.utils.isScrolled
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.runBlocking

@Composable
fun HomeFeedScreen(
    uiState: HomeUiState,
    showTopAppBar: Boolean,
    onSelectPost: (String) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onRefreshPosts: () -> Unit,
    onErrorDismiss: (Long) -> Unit,
    openDrawer: () -> Unit,
    homeListLazyListState: LazyListState,
    scaffoldState: ScaffoldState,
    modifier: Modifier = Modifier
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

        PostList(
            postsFeed = hasPostsUiState.postsFeed,
            favorites = hasPostsUiState.favorites,
            showExpandedSearch = !showTopAppBar,
            onArticleTapped = onSelectPost,
            onToggleFavorite = onToggleFavorite,
            contentPadding = rememberContentPaddingForScreen(
                additionalTop = if (showTopAppBar) {
                    0.dp
                } else {
                    8.dp
                }
            ),
            modifier = contentModifier,
            state = homeListLazyListState
        )

    }


}

@Composable
fun PostList(
    postsFeed: PostsFeed,
    favorites: Set<String>,
    showExpandedSearch: Boolean,
    onArticleTapped: (String) -> Unit,
    onToggleFavorite: (String) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier,
    state: LazyListState
) {

    LazyColumn(modifier = modifier, contentPadding = contentPadding, state = state) {
        if (showExpandedSearch) {
            item { HomeSearch(Modifier.padding(horizontal = 16.dp)) }
        }
        item { PostListTopSection(postsFeed.highlightedPost, onArticleTapped) }
        if (postsFeed.recommendedPosts.isNotEmpty()) {
            item {
                PostListSimpleSection(
                    postsFeed.recommendedPosts,
                    onArticleTapped,
                    favorites,
                    onToggleFavorite
                )
            }
        }
        if (postsFeed.popularPosts.isNotEmpty()) {
            item {
                PostListPopularSection(postsFeed.popularPosts, navigateToArticle = onArticleTapped)
            }
        }
        if (postsFeed.recentPosts.isNotEmpty()) {
            item {
                PostListHistorySection(postsFeed.popularPosts, navigateToArticle = onArticleTapped)
            }
        }
    }


}

@Composable
fun PostListHistorySection(posts: List<Post>, navigateToArticle: (String) -> Unit) {

    Column {
        posts.forEach {
            PostCardHistory(it,navigateToArticle)
            PostListDivider()
        }
    }

}


@Composable
fun PostListPopularSection(popularPosts: List<Post>, navigateToArticle: (String) -> Unit) {
    Column {
        Text(
            modifier = Modifier.padding(16.dp),
            text = stringResource(id = R.string.home_popular_section_title),
            style = MaterialTheme.typography.subtitle1
        )
        LazyRow(modifier = Modifier.padding(end = 16.dp)) {
            items(popularPosts) { post ->
                PostCardPopular(
                    post,
                    navigateToArticle,
                    Modifier.padding(start = 16.dp, bottom = 16.dp)
                )
                PostListDivider()
            }
        }
    }
}


@Composable
fun PostListSimpleSection(
    recommendedPosts: List<Post>,
    onArticleTapped: (String) -> Unit,
    favorites: Set<String>,
    onToggleFavorite: (String) -> Unit
) {

    Column {
        recommendedPosts.forEach { post ->
            PostCardSimple(
                post = post,
                isFavorite = favorites.contains(post.id),
                navigateToArticle = { onArticleTapped(post.id) },
                onToggleFavorite = { onToggleFavorite(post.id) }
            )
            PostListDivider()
        }
    }


}

@Composable
private fun PostListDivider() {
    Divider(
        modifier = Modifier.padding(horizontal = 14.dp),
        color = MaterialTheme.colors.onSurface.copy(alpha = 0.08f)
    )
}

@Composable
fun PostListTopSection(posts: Post, onArticleTapped: (String) -> Unit) {
    Text(
        modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp),
        text = stringResource(id = R.string.home_top_section_title),
        style = MaterialTheme.typography.subtitle1
    )
    PostCardTop(post = posts)
}

@Composable
fun HomeSearch(modifier: Modifier) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(Dp.Hairline, MaterialTheme.colors.onSurface.copy(alpha = .6f)),
        elevation = 4.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {

                IconButton(onClick = { /*TODO*/ }) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = stringResource(id = R.string.cd_search)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(id = R.string.home_search),
                    style = MaterialTheme.typography.body2
                )
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(
                        imageVector = Icons.Filled.MoreVert,
                        contentDescription = stringResource(id = R.string.cd_more_actions)
                    )
                }
            }

        }
    }
}

@Composable
fun HomeScreenWithList(
    uiState: HomeUiState,
    showTopAppBar: Boolean,
    onRefreshPosts: () -> Unit,
    onErrorDismiss: (Long) -> Unit,
    openDrawer: () -> Unit,
    homeListLazyListState: LazyListState,
    scaffoldState: ScaffoldState,
    modifier: Modifier =Modifier,
    hasPostsContent: @Composable (uiState: HomeUiState.HasPosts, modifier: Modifier) -> Unit
) {
    Scaffold(
        scaffoldState = scaffoldState,
        snackbarHost = { JetnewsSnackbarHost(hostState = it) },
        topBar = {
            if (showTopAppBar) {
                HomeTopAppBar(
                    openDrawer,
                    elevation = if (!homeListLazyListState.isScrolled) 0.dp else 4.dp
                )
            }
        }, modifier = modifier
    ) { paddingValues ->
        val contentModifier = Modifier.padding(paddingValues)
        LoadingContent(
            empty = when (uiState) {
                is HomeUiState.NoPosts -> uiState.isLoading
                is HomeUiState.HasPosts -> false
            },
            emptyContent = {
                FullScreenLoading()
            },
            onRefresh = onRefreshPosts,
            content = {
                when (uiState) {
                    is HomeUiState.HasPosts -> hasPostsContent(uiState, contentModifier)
                    is HomeUiState.NoPosts -> {
                        if (uiState.errorMessages.isEmpty()) {
                            TextButton(onClick = onRefreshPosts, modifier.fillMaxSize()) {
                                Text(
                                    stringResource(id = R.string.home_tap_to_load_content),
                                    textAlign = TextAlign.Center
                                )
                            }
                        } else {
                            // there's currently an error showing, don't show any content
                            Box(contentModifier.fillMaxSize()) { /* empty screen */ }
                        }
                    }
                }
            },
            loading = uiState.isLoading
        )
    }
    if (uiState.errorMessages.isNotEmpty()) {

        val errorMessage = remember(uiState) {
            uiState.errorMessages[0]
        }

        val errorMessageText = stringResource(errorMessage.messageId)
        val retryMessageText = stringResource(id = R.string.retry)

        //如果在 LaunchedEffect 运行时 onRefreshPosts 或 onErrorDismiss 发生变化，
        // 请不要重新启动效果并使用最新的 lambda 值
        val onRefreshPostsState by rememberUpdatedState(newValue = onRefreshPosts)
        val onErrorDismissState by rememberUpdatedState(newValue = onErrorDismiss)
        LaunchedEffect(errorMessageText, retryMessageText, scaffoldState) {
            val snackBarResult = scaffoldState.snackbarHostState.showSnackbar(
                message = errorMessageText, actionLabel = retryMessageText
            )
            if (snackBarResult == SnackbarResult.ActionPerformed) {
                onRefreshPostsState()
            }
            //消息显示并关闭后，通知 ViewModel
            onErrorDismissState(errorMessage.id)
        }
    }

}


@Composable
fun FullScreenLoading() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center)
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun LoadingContent(
    empty: Boolean,
    emptyContent: @Composable () -> Unit,
    loading: Boolean,
    onRefresh: () -> Unit,
    content: @Composable () -> Unit
) {
    if (empty) {
        emptyContent()
    } else {
        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing = loading),
            onRefresh = onRefresh,
            content = content
        )
    }


}

/**
 * Icon 与 IconButton 区别
 */
@Composable
fun HomeTopAppBar(openDrawer: () -> Unit, elevation: Dp) {
    val title = stringResource(id = R.string.app_name)
    TopAppBar(title = {
        Icon(
            painter = painterResource(R.drawable.ic_jetnews_wordmark),
            contentDescription = title,
            tint = MaterialTheme.colors.onBackground,
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 4.dp, top = 10.dp)
        )
    }, navigationIcon = {
        IconButton(onClick = openDrawer) {
            Icon(
                painter = painterResource(R.drawable.ic_jetnews_logo),
                contentDescription = stringResource(R.string.cd_open_navigation_drawer),
                tint = MaterialTheme.colors.primary
            )
        }
    }, actions = {
        IconButton(onClick = { /*TODO*/ }) {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = stringResource(id = R.string.cd_search)
            )
        }
    }, backgroundColor = MaterialTheme.colors.surface, elevation = elevation)
}

@Preview("Home list navrail screen", device = Devices.NEXUS_7_2013)
@Preview(
    "Home list navrail screen (dark)",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    device = Devices.NEXUS_7_2013
)
@Preview("Home list navrail screen (big font)", fontScale = 1.5f, device = Devices.NEXUS_7_2013)
@Composable
fun PreviewHomeListNavRailScreen() {
    val postsFeed = runBlocking {
        (BlockingFakePostsRepository().getPostsFeed() as Response.Success).data
    }
    Week4Theme() {
        HomeFeedScreen(
            uiState = HomeUiState.HasPosts(
                postsFeed = postsFeed,
                selectedPost = postsFeed.highlightedPost,
                isArticleOpen = false,
                favorites = emptySet(),
                isLoading = false,
                errorMessages = emptyList()
            ),
            showTopAppBar = true,
            onToggleFavorite = {},
            onSelectPost = {},
            onRefreshPosts = {},
            onErrorDismiss = {},
            openDrawer = {},
            homeListLazyListState = rememberLazyListState(),
            scaffoldState = rememberScaffoldState()
        )
    }
}


//@Preview("Home list detail screen", device = Devices.PIXEL_C)
//@Preview("Home list detail screen (dark)", uiMode = Configuration.UI_MODE_NIGHT_YES, device = Devices.PIXEL_C)
//@Preview("Home list detail screen (big font)", fontScale = 1.5f, device = Devices.PIXEL_C)
//@Composable
//fun PreviewHomeListDetailScreen() {
//    val postsFeed = runBlocking {
//        (BlockingFakePostsRepository().getPostsFeed() as Response.Success).data
//    }
//    Week4Theme() {
//        HomeFeedWithArticleDetailsScreen(
//            uiState = HomeUiState.HasPosts(
//                postsFeed = postsFeed,
//                selectedPost = postsFeed.highlightedPost,
//                isArticleOpen = false,
//                favorites = emptySet(),
//                isLoading = false,
//                errorMessages = emptyList()
//            ),
//            showTopAppBar = true,
//            onToggleFavorite = {},
//            onSelectPost = {},
//            onRefreshPosts = {},
//            onErrorDismiss = {},
//            onInteractWithList = {},
//            onInteractWithDetail = {},
//            openDrawer = {},
//            homeListLazyListState = rememberLazyListState(),
//            articleDetailLazyListStates = postsFeed.allPosts.associate { post ->
//                key(post.id) {
//                    post.id to rememberLazyListState()
//                }
//            },
//            scaffoldState = rememberScaffoldState()
//        )
//    }
//}
