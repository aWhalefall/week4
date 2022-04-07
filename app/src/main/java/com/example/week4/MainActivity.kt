package com.example.week4

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import com.example.week4.ui.JetnewsApp
import com.example.week4.ui.home.HomeScreen
import com.example.week4.ui.home.JetNewAppTopBar
import com.example.week4.ui.theme.Week4Theme
import com.example.week4.utils.rememberWindowSizeClass


class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //访问window的Helper 类
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val appContainer = (application as BaseApplication).container
        setContent {
            val windowSizeClass = rememberWindowSizeClass()
            Week4Theme {
              JetnewsApp(appContainer = appContainer, windowSize = windowSizeClass)
            }
        }
    }
}
