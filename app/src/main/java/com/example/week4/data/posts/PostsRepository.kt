package com.example.week4.data.posts

import com.example.week4.data.Response
import com.example.week4.model.Post
import com.example.week4.model.PostsFeed
import kotlinx.coroutines.flow.Flow


interface PostsRepository {

    /**
     * Get a specific JetNews post.
     */
    suspend fun getPost(postId: String?): Response<Post>

    /**
     * Get JetNews posts.
     */
    suspend fun getPostsFeed(): Response<PostsFeed>

    /**
     * Observe the current favorites
     */
    fun observeFavorites(): Flow<Set<String>>

    /**
     * 将 postId 切换为收藏或不收藏
     * Toggle a postId to be a favorite or not.
     */
    suspend fun toggleFavorite(postId: String)
}
