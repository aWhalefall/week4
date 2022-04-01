package com.example.week4.data

import android.content.Context
import com.example.week4.data.interests.InterestsRepository
import com.example.week4.data.interests.impl.FakeInterestsRepository
import com.example.week4.data.interests.impl.FakePostsRepository
import com.example.week4.data.posts.PostsRepository

/**
 * author：yangweichao@reworldgame.com
 * data: 2022/3/31 18:59
 * 应用层的依赖注入容器
 */

interface AppContainer {
    val postsRepository: PostsRepository
    val interestsRepository: InterestsRepository
}

class AppContainerImpl(private val applicationContext: Context) : AppContainer {

    override val postsRepository: PostsRepository by lazy {
        FakePostsRepository()
    }

    override val interestsRepository: InterestsRepository by lazy {
        FakeInterestsRepository()
    }


}