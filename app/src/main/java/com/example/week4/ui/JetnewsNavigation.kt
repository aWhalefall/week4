package com.example.week4.ui

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController

/**
 * author：yangweichao@reworldgame.com
 * data: 2022/4/2 10:51
 * jetNet导航目的地
 */
object JetnewsDestinations {
    const val HOME_ROUTE = "home"
    const val INTERESTS_ROUTE = "interests"
}

class JetnewsNavigationActions(navHostController: NavHostController) {

    val navigateToHome: () -> Unit = {
        navHostController.navigate(JetnewsDestinations.HOME_ROUTE) {
            popUpTo(navHostController.graph.findStartDestination().id) {
                saveState = true
            }
            //启动方式
            launchSingleTop = true
            //重新选择先前的状态时恢复先前状态
            restoreState = true
        }
    }

    val navigateToInterests: () -> Unit = {
        navHostController.navigate(JetnewsDestinations.INTERESTS_ROUTE) {
            popUpTo(navHostController.graph.findStartDestination().id) {
                saveState = true
            }
            //启动方式
            launchSingleTop = true
            //重新选择先前的状态时恢复先前状态
            restoreState = true
        }
    }


}
