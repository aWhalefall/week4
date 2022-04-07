package com.example.week4.ui.interests

import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable


@Composable
fun InterestsRoute(
    interestsViewModel: InterestsViewModel,
    isExpandedScreen: Boolean,
    openDrawer: () -> Unit,
    scaffoldState: ScaffoldState = rememberScaffoldState()
) {

    val tabContent = rememberTabContent(interestsViewModel)
    val (currentSection, updateSection) = rememberSaveable {
        mutableStateOf(tabContent.first().section)
    }

}