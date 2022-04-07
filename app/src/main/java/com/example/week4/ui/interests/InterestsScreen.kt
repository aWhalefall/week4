package com.example.week4.ui.interests

import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth
import androidx.compose.ui.unit.dp
import com.example.jetnews.R
import com.example.week4.data.interests.InterestSection
import com.example.week4.data.interests.TopicSelection
import com.google.accompanist.insets.navigationBarsPadding
import kotlin.math.max

enum class Sections(@StringRes val titleResId: Int) {
    Topics(R.string.interests_section_topics), People(R.string.interests_section_people), Publications(
        R.string.interests_section_publications
    )
}


class TabContent(val section: Sections, val content: @Composable () -> Unit)

//tag 状态容器
@Composable
fun rememberTabContent(interestsViewModel: InterestsViewModel): List<TabContent> {
    //tag 接收流
    val uiState by interestsViewModel.uiState.collectAsState()

    // Describe the screen sections here since each section needs 2 states and 1 event.
    // Pass them to the stateless InterestsScreen using a tabContent.
    val topicsSection = TabContent(Sections.Topics) {
        val selectedTopics by interestsViewModel.selectedTopics.collectAsState()
        TabWithSections(sections = uiState.topics,
            selectedTopics = selectedTopics,
            onTopicSelect = { interestsViewModel.toggleTopicSelection(it) })
    }

    val peopleSection = TabContent(Sections.People) {
        val selectedPeople by interestsViewModel.selectedPeople.collectAsState()
        TabWithTopics(topics = uiState.people,
            selectedTopics = selectedPeople,
            onTopicSelect = { interestsViewModel.togglePersonSelected(it) })
    }

    val publicationSection = TabContent(Sections.Publications) {
        val selectedPublications by interestsViewModel.selectedPublications.collectAsState()
        TabWithTopics(topics = uiState.publications,
            selectedTopics = selectedPublications,
            onTopicSelect = { interestsViewModel.togglePublicationSelected(it) })
    }

    return listOf(topicsSection, peopleSection, publicationSection)

}

@Composable
fun TabWithTopics(
    topics: List<String>,
    selectedTopics: Set<TopicSelection>,
    onTopicSelect: (String) -> Unit
) {


}

/**
 * Modifier for UI containers that show interests items
 */
private val tabContainerModifier = Modifier
    .fillMaxWidth()
    .wrapContentWidth(Alignment.CenterHorizontally)
    .navigationBarsPadding(start = false, end = false)


@Composable
fun TabWithSections(
    sections: List<InterestSection>,
    selectedTopics: Set<TopicSelection>,
    onTopicSelect: (TopicSelection) -> Unit
) {

    Column(tabContainerModifier.verticalScroll(rememberScrollState())) {
        sections.forEach { (section, topics) ->
            Text(
                text = section,
                modifier = Modifier
                    .padding(16.dp)
                    .semantics { heading() },
                style = MaterialTheme.typography.subtitle1
            )
            InterestsAdaptiveContentLayout {
                topics.forEach { topic ->
                    TopicItem(
                        topic = topic,
                        selected = selectedTopics.contains(
                            TopicSelection(
                                section = section,
                                topic = topic
                            )
                        ),
                        onTopicSelect = {
                            onTopicSelect(
                                TopicSelection(
                                    section = section,
                                    topic = topic
                                )
                            )
                        })

                }
            }
        }


    }
}

/**
 * tag 自定义布局
 * Custom layout for the Interests screen that places items on the screen given the available size.
 *
 * For example: Given a list of items (A, B, C, D, E) and a screen size that allows 2 columns,
 * the items will be displayed on the screen as follows:
 *     A B
 *     C D
 *     E
 */
@Composable
private fun InterestsAdaptiveContentLayout(
    modifier: Modifier = Modifier,
    topPadding: Dp = 0.dp,
    itemSpacing: Dp = 4.dp,
    itemMaxWidth: Dp = 450.dp,
    multipleColumnsBreakPoint: Dp = 600.dp,
    content: @Composable () -> Unit,
) {
    Layout(modifier = modifier, content = content) { measurables, outerConstraints ->
        // Convert parameters to Px. Safe to do as `Layout` measure block runs in a `Density` scope
        val multipleColumnsBreakPointPx = multipleColumnsBreakPoint.roundToPx()
        val topPaddingPx = topPadding.roundToPx()
        val itemSpacingPx = itemSpacing.roundToPx()
        val itemMaxWidthPx = itemMaxWidth.roundToPx()

        // Number of columns to display on the screen. This is harcoded to 2 due to
        // the design mocks, but this logic could change in the future.
        val columns = if (outerConstraints.maxWidth < multipleColumnsBreakPointPx) 1 else 2
        // Max width for each item taking into account available space, spacing and `itemMaxWidth`
        val itemWidth = if (columns == 1) {
            outerConstraints.maxWidth
        } else {
            val maxWidthWithSpaces = outerConstraints.maxWidth - (columns - 1) * itemSpacingPx
            (maxWidthWithSpaces / columns).coerceIn(0, itemMaxWidthPx)
        }
        val itemConstraints = outerConstraints.copy(maxWidth = itemWidth)

        // Keep track of the height of each row to calculate the layout's final size
        val rowHeights = IntArray(measurables.size / columns + 1)
        // Measure elements with their maximum width and keep track of the height
        val placeables = measurables.mapIndexed { index, measureable ->
            val placeable = measureable.measure(itemConstraints)
            // Update the height for each row
            val row = index.floorDiv(columns)
            rowHeights[row] = max(rowHeights[row], placeable.height)
            placeable
        }

        // Calculate maxHeight of the Interests layout. Heights of the row + top padding
        val layoutHeight = topPaddingPx + rowHeights.sum()
        // Calculate maxWidth of the Interests layout
        val layoutWidth = itemWidth * columns + (itemSpacingPx * (columns - 1))

        // Lay out given the max width and height
        layout(
            width = outerConstraints.constrainWidth(layoutWidth),
            height = outerConstraints.constrainHeight(layoutHeight)
        ) {
            // Track the y co-ord we have placed children up to
            var yPosition = topPaddingPx
            // Split placeables in lists that don't exceed the number of columns
            // and place them taking into account their width and spacing
            placeables.chunked(columns).forEachIndexed { rowIndex, row ->
                var xPosition = 0
                row.forEach { placeable ->
                    placeable.placeRelative(x = xPosition, y = yPosition)
                    xPosition += placeable.width + itemSpacingPx
                }
                yPosition += rowHeights[rowIndex]
            }
        }
    }
}

@Composable
fun TopicItem(
    topic: String,
    selected: Boolean,
    onTopicSelect: () -> Unit,
    modifier: Modifier = Modifier
) {

    Column(modifier.padding(horizontal = 16.dp)) {

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = R.drawable.placeholder_1_1),
                contentDescription = null
            )
            Text(text = topic, modifier = Modifier.weight(1f))
            IconToggleButton(checked = selected, onCheckedChange = { onTopicSelect }) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = null)
            }
        }

        Divider(
            modifier = Modifier.padding(start = 72.dp, top = 8.dp, bottom = 8.dp),
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.1f)
        )

    }

}
