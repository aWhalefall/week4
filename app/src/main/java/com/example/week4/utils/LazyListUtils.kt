package com.example.week4.utils

import androidx.compose.foundation.lazy.LazyListState

/**
 * author：yangweichao@reworldgame.com
 * data: 2022/4/6 14:51
 * { } 代码块需要给出返回值
 * 表达式直接作为返回值
 */
val LazyListState.isScrolled: Boolean
    get() = firstVisibleItemIndex > 0 || firstVisibleItemScrollOffset > 0
