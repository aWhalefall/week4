package com.example.week4.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.week4.BaseApplication
import com.example.week4.data.AppContainer
import com.example.week4.ui.components.AppNavRail
import com.example.week4.ui.theme.Week4Theme
import com.example.week4.utils.WindowSize
import com.google.accompanist.insets.*
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.launch

@Composable
fun JetnewsApp(appContainer: AppContainer, windowSize: WindowSize?) {
    Week4Theme() {
        ProvideWindowInsets {
            val systemUiController = rememberSystemUiController()
            val darkIcons = MaterialTheme.colors.isLight
            SideEffect {
                systemUiController.setSystemBarsColor(Color.Transparent, darkIcons = darkIcons)
            }
            val navContainer = rememberNavController()
            val navigationActions = remember(navContainer) {
                JetnewsNavigationActions(navContainer)
            }
            //在可组合项外启动携程
            val coroutineScope = rememberCoroutineScope()
            //需要清理的效应.目的地切换后状态	 State<NavBackStackEntry?>
            val navBackStackEntry by navContainer.currentBackStackEntryAsState()
            val currentRoute =
                navBackStackEntry?.destination?.route ?: JetnewsDestinations.HOME_ROUTE

            //平板横向
            val isExpandedScreen = windowSize == WindowSize.Expanded
            //感知抽屉状态
            val sizeAwareDrawerState = rememberSizeAwareDrawerState(isExpandedScreen)

            ModalDrawer(
                drawerState = sizeAwareDrawerState,
                drawerContent = {
                    AppDrawer(currentRoute,
                        navigateToHome = { navigationActions.navigateToHome },
                        navigateToInterests = { navigationActions.navigateToInterests },
                        modifier = Modifier
                            .statusBarsPadding()
                            .navigationBarsPadding(), closeDrawer = {
                            coroutineScope.launch {
                                sizeAwareDrawerState.close()
                            }
                        })
                }, gesturesEnabled = !isExpandedScreen
            ) {
                Row(
                    Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .navigationBarsPadding(bottom = false)
                ) {
                    //侧面导航,用于横向平板
                    if (isExpandedScreen) {
                        AppNavRail(
                            currentRoute = currentRoute,
                            navigateToHome = { navigationActions.navigateToHome },
                            navigateToInterests = { navigationActions.navigateToInterests }
                        )
                    }
                    JetnewsNavGraph(
                        appContainer = appContainer,
                        isExpandedScreen = isExpandedScreen,
                        navController = navContainer, openDrawer = {
                            coroutineScope.launch {
                                sizeAwareDrawerState.open()
                            }
                        }
                    )

                }


            }
        }
    }
}

@Composable
fun rememberSizeAwareDrawerState(isExpandedScreen: Boolean): DrawerState {
    //横竖屏切换状态可保存
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    return if (isExpandedScreen) {
        DrawerState(DrawerValue.Closed)
    } else {
        drawerState
    }

}

@Composable
fun rememberContentPaddingForScreen(additionalTop: Dp = 0.dp) = rememberInsetsPaddingValues(
    insets = LocalWindowInsets.current.systemBars,
    applyTop = false, applyEnd = false, applyStart = false, additionalTop = additionalTop
)
