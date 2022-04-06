package com.example.week4.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jetnews.R
import com.example.week4.data.interests.impl.post3
import com.example.week4.model.Post
import com.example.week4.ui.theme.Week4Theme


@Composable
fun PostCardSimple(
    post: Post,
    isFavorite: Boolean,
    navigateToArticle: (String) -> Unit,
    onToggleFavorite: () -> Unit
) {

    val bookmarkAction =
        stringResource(id = if (isFavorite) R.string.unbookmark else R.string.bookmark)

    Row(
        modifier = Modifier
            .clickable(onClick = { navigateToArticle(post.id) })
            .padding(16.dp)
            .semantics {
                //通用语义属性，主要用于可访问性和测试
                customActions = listOf(
                    CustomAccessibilityAction(
                        label = bookmarkAction,
                        action = { onToggleFavorite();true })
                )

            }) {

        PostImage(post = post, modifier = Modifier.padding(end = 16.dp))
        Column(modifier = Modifier.weight(1f)) {
            PostTitle(post)
            AuthorAndReadTime(post)
        }
        //删除按钮语义，以便可以在行级别处理操作
        BookmarkButton(
            isBookmarked = isFavorite,
            onClick = onToggleFavorite,
            modifier = Modifier.clearAndSetSemantics {},
            contentAlpha = ContentAlpha.medium
        )

    }

}

@Composable
fun BookmarkButton(
    isBookmarked: Boolean,
    onClick: () -> Unit,
    modifier: Modifier,
    contentAlpha: Float
) {
    val clickLabel =
        stringResource(id = if (isBookmarked) R.string.unbookmark else R.string.bookmark)
    CompositionLocalProvider(LocalContentAlpha provides contentAlpha) {

        //使用无障碍服务可以与用户交流的自定义点击标签。我们只想覆盖标签，而不是实际的动作，所以对于动作我们传递 null
        IconToggleButton(
            checked = isBookmarked,
            onCheckedChange = { onClick() },
            modifier = modifier.semantics {
                this.onClick(label = clickLabel, action = null)
            }) {

            Icon(
                imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                contentDescription = null // handled by click label of parent
            )

        }

    }
}

@Composable
fun AuthorAndReadTime(post: Post, modifier: Modifier = Modifier) {

    Row(modifier) {
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Text(
                text = stringResource(
                    id = R.string.home_post_min_read,
                    formatArgs = arrayOf(
                        post.metadata.author.name,
                        post.metadata.readTimeMinutes
                    )
                ),
                style = MaterialTheme.typography.body2
            )

        }
    }
}

@Composable
fun PostTitle(post: Post) {
    Text(post.title, style = MaterialTheme.typography.subtitle1)
}

@Composable
fun PostImage(modifier: Modifier = Modifier, post: Post) {
    Image(
        painter = painterResource(id = post.imageThumbId),
        contentDescription = null,
        modifier = modifier
            .size(40.dp, 40.dp)
            .clip(
                MaterialTheme.shapes.small
            )
    )
}

@Composable
fun PostCardHistory(post: Post, navigateToArticle: (String) -> Unit) {
    var openDialog by remember { mutableStateOf(false) }
    Row(
        Modifier
            .clickable(onClick = { navigateToArticle(post.id) })
    ) {
        PostImage(
            post = post,
            modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp)
        )
        Column(
            Modifier
                .weight(1f)
                .padding(top = 16.dp, bottom = 16.dp)
        ) {
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text(
                    text = stringResource(id = R.string.home_post_based_on_history),
                    style = MaterialTheme.typography.overline
                )
            }
            PostTitle(post = post)
            AuthorAndReadTime(
                post = post,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            IconButton(onClick = { openDialog = true }) {
                Icon(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = stringResource(R.string.cd_more_actions),
                )
            }
        }

        if (openDialog) {
            AlertDialog(
                modifier = Modifier.padding(20.dp),
                onDismissRequest = { openDialog = false },
                title = {
                    Text(
                        text = stringResource(id = R.string.fewer_stories),
                        style = MaterialTheme.typography.h6
                    )
                },
                text = {
                    Text(
                        text = stringResource(id = R.string.fewer_stories_content),
                        style = MaterialTheme.typography.body1
                    )
                },
                confirmButton = {
                    Text(
                        text = stringResource(id = R.string.agree),
                        style = MaterialTheme.typography.button,
                        color = MaterialTheme.colors.primary,
                        modifier = Modifier
                            .padding(15.dp)
                            .clickable { openDialog = false }
                    )
                }
            )
        }


    }

}

@Preview("Post History card")
@Composable
fun HistoryPostPreview() {
    Week4Theme() {
        Surface {
            PostCardHistory(post3, {})
        }
    }
}
