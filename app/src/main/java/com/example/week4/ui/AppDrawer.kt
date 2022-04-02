package com.example.week4.ui

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jetnews.R;
import com.example.week4.ui.theme.Week4Theme

@Composable
fun AppDrawer(
    currentRoute: String,
    navigateToHome: () -> Unit,
    navigateToInterests: () -> Unit,
    closeDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier.fillMaxSize()) {
        JetNewsLogo(modifier = Modifier.padding(16.dp))
        Divider(color = MaterialTheme.colors.onSurface.copy(.2f))
        DrawerButton(
            icon = Icons.Filled.Home,
            label = stringResource(id = R.string.home_title),
            isSelected = currentRoute == JetnewsDestinations.HOME_ROUTE,
            action = {
                navigateToHome()
                closeDrawer()
            }
        )
        DrawerButton(
            icon = Icons.Filled.ListAlt,
            label = stringResource(id = R.string.interests_title),
            isSelected = currentRoute == JetnewsDestinations.INTERESTS_ROUTE,
            action = {
                navigateToInterests()
                closeDrawer()
            }
        )
    }
}


@Preview
@Composable
fun DrawerButtonPre() {
    DrawerButton(
        icon = Icons.Filled.Home,
        label = stringResource(id = R.string.home_title),
        isSelected = true,
        action = {
        }
    )
}


@SuppressLint("SuspiciousIndentation")
@Composable
fun DrawerButton(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    action: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colors
    val textIconColor = if (isSelected) {
        colors.primary
    } else {
        colors.onSurface.copy(alpha = 0.6f)
    }
    val backgroundColor = if (isSelected) {
        colors.primary.copy(alpha = 0.12f)
    } else {
        Color.Transparent
    }
    val surfaceModifier = modifier
        .padding(start = 8.dp, top = 8.dp, end = 8.dp)
        .fillMaxWidth()
    //背景+小圆角
    Surface(
        modifier = surfaceModifier,
        color = backgroundColor,
        shape = MaterialTheme.shapes.small
    ) {
        //Modifier 控制content内组件offsest，padding 也是控制内部。如果想控制外部需要surface或者Row
        //row 具有方向,surface 不具有方向
        TextButton(onClick = action, modifier = Modifier.fillMaxWidth()) {
            Row(
                verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()
            ) {
                //contentDescription 走默认即可不需要作为入参嵌套传递
                NavigationIcon(
                    icon = icon,
                    isSelected = isSelected,
                    modifier = modifier,
                    tintColor = textIconColor,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = label, color = textIconColor, style = MaterialTheme.typography.body2)
            }
        }

    }

}


@Composable
fun NavigationIcon(
    icon: ImageVector,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    tintColor: Color? = null
) {

    val imageAlpha = if (isSelected) {
        1f
    } else {
        0.6f
    }
    val iconTintColor = tintColor ?: if (isSelected) {
        MaterialTheme.colors.primary
    } else {
        MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
    }

    Image(
        modifier = modifier,
        imageVector = icon,
        contentDescription = contentDescription,
        contentScale = ContentScale.Inside,
        colorFilter = ColorFilter.tint(iconTintColor),
        alpha = imageAlpha
    )

}

@Composable
fun JetNewsLogo(modifier: Modifier) {
    Row(modifier = modifier) {
        JetNewsIcon()
        Spacer(Modifier.width(8.dp))
        Image(
            painter = painterResource(id = R.drawable.ic_jetnews_wordmark),
            contentDescription = null,
            colorFilter = ColorFilter.tint(MaterialTheme.colors.onSurface)
        )
    }
}

//入参为什么老是带modifier: Modifier = Modifier，放入自己可组合函数写死。单独组件不应用其他地方
@Composable
fun JetNewsIcon(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(R.drawable.ic_jetnews_logo),
        contentDescription = null,
        colorFilter = ColorFilter.tint(MaterialTheme.colors.primary),
        modifier = modifier
    )
}

@Preview("Drawer contents")
@Preview("Drawer contents (dark)", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewAppDrawer() {
    Week4Theme {
        Surface {
            AppDrawer(
                currentRoute = JetnewsDestinations.HOME_ROUTE,
                navigateToHome = {},
                navigateToInterests = {},
                closeDrawer = { }
            )
        }
    }
}

