package com.example.week4.data.posts.impl

import com.example.week4.data.Response
import com.example.week4.data.interests.impl.posts
import com.example.week4.data.posts.PostsRepository
import com.example.week4.model.Post
import com.example.week4.model.PostsFeed
import com.example.week4.utils.addOrRemove
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import kotlin.contracts.Returns

/**
 * author：yangweichao@reworldgame.com
 * data: 2022/4/6 15:42
 * PostsRepository 的实现，它同步返回带有资源的硬编码帖子列表。
 */

class BlockingFakePostsRepository : PostsRepository {

    private val favorites = MutableStateFlow<Set<String>>(setOf())

    override suspend fun getPost(postId: String?): Response<Post> {
        return withContext(Dispatchers.IO) {
            val post = posts.allPosts.find { it.id == postId }
            if (post == null) {
                Response.Error(IllegalArgumentException("Unable to find post"))
            } else {
                Response.Success(post)
            }
        }
    }

    override suspend fun getPostsFeed(): Response<PostsFeed> {
        return Response.Success(posts)
    }

    override fun observeFavorites(): Flow<Set<String>> {
        return favorites
    }

    override suspend fun toggleFavorite(postId: String) {
        val set = favorites.value.toMutableSet()
        set.addOrRemove(postId)
        favorites.value = set
    }

}
