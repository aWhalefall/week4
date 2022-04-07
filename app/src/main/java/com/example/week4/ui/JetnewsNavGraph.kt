package com.example.week4.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.week4.data.AppContainer
import com.example.week4.data.interests.InterestsRepository
import com.example.week4.ui.home.HomeRoute
import com.example.week4.ui.home.HomeViewModel
import com.example.week4.ui.interests.InterestsRoute
import com.example.week4.ui.interests.InterestsViewModel

/**
 * author：yangweichao@reworldgame.com
 * data: 2022/4/2 16:31
 * 传递参数未知情况
 */
@Composable
fun JetnewsNavGraph(
    modifier: Modifier = Modifier,
    isExpandedScreen: Boolean,
    appContainer: AppContainer,
    navController: NavHostController = rememberNavController(),
    openDrawer: () -> Unit = {},
    startDestination: String = JetnewsDestinations.HOME_ROUTE
) {

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(JetnewsDestinations.HOME_ROUTE) {
            val homeViewModel: HomeViewModel = viewModel(
                factory = HomeViewModel.provideFactory(appContainer.postsRepository)
            )
            HomeRoute(
                homeViewModel = homeViewModel,
                isExpandedScreen = isExpandedScreen,
                openDrawer = openDrawer
            )
        }
        composable(JetnewsDestinations.INTERESTS_ROUTE) {
            val interestsViewModel: InterestsViewModel = viewModel(
                factory = InterestsViewModel.provideFactory(appContainer.interestsRepository)
            )
            InterestsRoute(
                interestsViewModel = interestsViewModel,
                isExpandedScreen = isExpandedScreen,
                openDrawer = openDrawer
            )
        }

    }


}