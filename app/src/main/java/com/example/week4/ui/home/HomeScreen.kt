package com.example.week4.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.jetnews.R


@Composable
fun JetNewAppTopBar() {
    TopAppBar(
        title = {
            Icon(
                painter = painterResource(id = R.drawable.ic_jetnews_wordmark),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 4.dp, top = 10.dp)
            )
        },
        navigationIcon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_jetnews_logo),
                contentDescription = null
            )
        },
        actions = {
            Icon(imageVector = Icons.Filled.Search, contentDescription = null)
        }, backgroundColor = MaterialTheme.colors.surface
    )
}

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {

    Column(modifier = modifier.fillMaxSize()) {

        
    }

}