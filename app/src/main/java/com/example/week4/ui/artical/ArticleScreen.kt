package com.example.week4.ui.artical

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.jetnews.R
import com.example.week4.model.Post
import com.example.week4.ui.utils.BookmarkButton
import com.example.week4.ui.utils.FavoriteButton
import com.example.week4.ui.utils.ShareButton
import com.example.week4.ui.utils.TextSettingsButton
import com.example.week4.utils.isScrolled
import com.google.accompanist.insets.navigationBarsPadding


/**
 * author：yangweichao@reworldgame.com
 * data: 2022/4/7 11:10
 * tag 分享文章
 */
fun sharePost(post: Post, context: Context) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TITLE, post.title)
        putExtra(Intent.EXTRA_TEXT, post.url)
    }
    context.startActivity(
        Intent.createChooser(
            intent,
            context.getString(R.string.article_share_post)
        )
    )

}

@Composable
fun ArticleScreen(
    onToggleFavorite: () -> Unit,
    isExpandedScreen: Boolean,
    post: Post,
    onBack: () -> Unit,
    isFavorite: Boolean,
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState()
) {
    var showUnimplementedActionDialog by rememberSaveable { mutableStateOf(false) }
    if (showUnimplementedActionDialog) {
        FunctionalityNotAvailablePopup { showUnimplementedActionDialog = false }
    }

    //tag 抽取compose 函数提示错误
    Row(modifier.fillMaxWidth()) {
        val context = LocalContext.current
        ArticleScreenContent(post = post,
            navigationIconContent = if (!isExpandedScreen) {
                {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_navigate_up),
                            tint = MaterialTheme.colors.primary
                        )
                    }
                }
            } else {
                null
            },
            lazyListState = lazyListState,
            bottomBarContent = if (isExpandedScreen) {
                {}
            } else {
                {
                    BottomBar(
                        onUnimplementedAction = { showUnimplementedActionDialog = true },
                        isFavorite = isFavorite,
                        onToggleFavorite = onToggleFavorite,
                        onSharePost = { sharePost(post = post, context = context) },
                        //tag  navigationBarsPadding 用法
                        modifier = Modifier.navigationBarsPadding(start = false, end = false)
                    )

                }
            }
        )
    }


}


@Composable
private fun BottomBar(
    onUnimplementedAction: () -> Unit,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onSharePost: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(elevation = 8.dp, modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .height(56.dp)
                .fillMaxWidth()
        ) {
            FavoriteButton(onClick = onUnimplementedAction)
            BookmarkButton(isBookmarked = isFavorite, onClick = onToggleFavorite)
            ShareButton(onClick = onSharePost)
            Spacer(modifier = Modifier.weight(1f))
            TextSettingsButton(onClick = onUnimplementedAction)
        }
    }
}

@Composable
fun ArticleScreenContent(
    post: Post,
    navigationIconContent: @Composable (() -> Unit)? = null,
    bottomBarContent: @Composable () -> Unit = {},
    lazyListState: LazyListState = rememberLazyListState()
) {
    Scaffold(topBar = {
        TopAppBar(
            title = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(align = Alignment.CenterHorizontally)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.icon_article_background),
                        contentDescription = null,
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(36.dp)
                    )
                    Text(
                        text = stringResource(
                            id = R.string.published_in,
                            post.publication?.name ?: ""
                        ),
                        style = MaterialTheme.typography.subtitle2,
                        color = LocalContentColor.current,
                        modifier = Modifier
                            .padding(start = 10.dp)
                            .weight(1.5f)
                    )

                }
            },
            backgroundColor = MaterialTheme.colors.surface,
            navigationIcon = navigationIconContent,
            elevation = if (lazyListState.isScrolled) 4.dp else 0.dp
        )
    }, bottomBar = bottomBarContent) {

            paddingValues ->
        PostContent(post = post, modifier = Modifier.padding(paddingValues), state = lazyListState)

    }

}


@Composable
private fun FunctionalityNotAvailablePopup(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        text = {
            Text(
                text = stringResource(id = R.string.article_functionality_not_available),
                style = MaterialTheme.typography.body2
            )
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(id = R.string.close))
            }
        }
    )
}