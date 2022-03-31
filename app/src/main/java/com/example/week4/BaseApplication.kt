package com.example.week4

import android.app.Application
import com.example.week4.data.AppContainer
import com.example.week4.data.AppContainerImpl

/**
 * author：yangweichao@reworldgame.com
 * data: 2022/3/31 18:57
 *
 */
class BaseApplication : Application() {

    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container= AppContainerImpl(this)
    }

}