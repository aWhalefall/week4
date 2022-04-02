package com.example.week4.data.interests.impl

import com.example.week4.data.Response
import com.example.week4.model.Post
import com.example.week4.data.posts.PostsRepository
import com.example.week4.model.PostsFeed
import com.example.week4.utils.addOrRemove
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

/**
 * author：yangweichao@reworldgame.com
 * data: 2022/4/1 10:20
 * 1.状态更新和获取（收集）
 */

class FakePostsRepository : PostsRepository {

    //可观察流.状态存储
    private val favorites = MutableStateFlow<Set<String>>(setOf())

    //用于使读取和更新状态的挂起函数可以安全地从任何线程调用
    private val mutex = Mutex()

    override suspend fun getPost(postId: String?): Response<Post> {
        return withContext(Dispatchers.IO) {
            val post = posts.allPosts.find { it.id == postId }
            if (post == null) {
                Response.Error(IllegalArgumentException("Post not found"))
            } else {
                Response.Success(post)
            }
        }
    }

    override suspend fun getPostsFeed(): Response<PostsFeed> {
        return withContext(Dispatchers.IO) {
            delay(800)
            if (shouldRandomlyFail()) {
                Response.Error(java.lang.IllegalStateException())
            } else {
                Response.Success(posts)
            }

        }
    }

    override fun observeFavorites(): Flow<Set<String>> = favorites

    override suspend fun toggleFavorite(postId: String) {
        mutex.withLock {
            //MutableStateFlow 转换为set
            val set = favorites.value.toMutableSet()
            set.addOrRemove(postId)
            //可变Set 转换为不可变Set
            favorites.value = set.toSet()
        }
    }

    // used to drive "random" failure in a predictable pattern, making the first request always
    // succeed
    private var requestCount = 0

    /**
     * Randomly fail some loads to simulate a real network.
     *
     * This will fail deterministically every 5 requests
     */
    private fun shouldRandomlyFail(): Boolean = ++requestCount % 5 == 0

}

