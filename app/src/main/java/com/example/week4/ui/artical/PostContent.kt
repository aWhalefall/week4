package com.example.week4.ui.artical

import android.content.res.Configuration
import android.graphics.drawable.shapes.Shape
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.week4.data.interests.impl.post3
import com.example.week4.model.*
import com.example.week4.ui.theme.Week4Theme


private val Colors.codeBlockBackground: Color
    get() = onSurface.copy(alpha = .15f)

private val defaultSpacerSize = 16.dp


@Composable
fun PostContent(
    post: Post,
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState()
) {
    LazyColumn(
        modifier = modifier.padding(
            horizontal = defaultSpacerSize
        ),
        state = state
    ) {
        postContentItems(post)
    }
}

fun LazyListScope.postContentItems(post: Post) {
    item {
        PostHeaderImage(post = post)
    }
    item {
        HeadTitle(post)
        Spacer(Modifier.height(defaultSpacerSize))
    }
    item {
        Row {
            Image(
                imageVector = Icons.Filled.AccountCircle,
                contentDescription = null,
                modifier = Modifier.size(40.dp)
            )
            Spacer(Modifier.width(defaultSpacerSize))
            Column {
                Text(text = post.metadata.author.name, style = MaterialTheme.typography.subtitle1)
                Text(
                    text = post.metadata.date,
                    style = MaterialTheme.typography.subtitle2,
                    modifier = Modifier.alpha(0.35f)
                )
            }
        }

    }
    items(post.paragraphs) {
        Paragraph(paragraphs = it)
    }

    item {
        Spacer(Modifier.height(48.dp))
    }

}


private data class ParagraphStyling(
    val textStyle: TextStyle,
    val paragraphStyle: ParagraphStyle,
    val trailingPadding: Dp
)

@Composable
fun Paragraph(paragraphs: Paragraph) {
    // TODO:  属性声明
    val (textStyle, paragraphStyle, trailingPadding) = paragraphs.type.getTextAndParagraphStyle()
    val annotatedString = paragraphToAnnotatedString(
        paragraphs,
        MaterialTheme.typography,
        MaterialTheme.colors.codeBlockBackground
    )

    Box(modifier = Modifier.padding(bottom = trailingPadding)) {

        when (paragraphs.type) {
            ParagraphType.Header -> {
                Text(
                    modifier = Modifier.padding(4.dp),
                    text = annotatedString,
                    style = textStyle.merge(paragraphStyle)
                )
            }
            ParagraphType.CodeBlock -> CodeBlockParagraph(
                text = annotatedString,
                textStyle = textStyle,
                paragraphStyle = paragraphStyle
            )
            ParagraphType.Bullet -> BulletParagraph(
                text = annotatedString,
                textStyle = textStyle,
                paragraphStyle = paragraphStyle
            )
            else -> Text(
                modifier = Modifier.padding(4.dp),
                text = annotatedString,
                style = textStyle
            )
        }
    }

}

@Composable
fun BulletParagraph(text: AnnotatedString, textStyle: TextStyle, paragraphStyle: ParagraphStyle) {
    Row {
        with(LocalDensity.current) {
            // this box is acting as a character, so it's sized with font scaling (sp)
            Box(
                modifier = Modifier
                    .size(8.sp.toDp(), 8.sp.toDp())
                    .alignBy {
                        // Add an alignment "baseline" 1sp below the bottom of the circle
                        9.sp.roundToPx()
                    }
                    .background(LocalContentColor.current, CircleShape),
            ) { /* no content */ }
        }
        Text(
            modifier = Modifier
                .weight(1f)
                .alignBy(FirstBaseline),
            text = text,
            style = textStyle.merge(paragraphStyle)
        )
    }

}

@Composable
fun CodeBlockParagraph(
    text: AnnotatedString,
    textStyle: TextStyle,
    paragraphStyle: ParagraphStyle
) {
    Surface(
        color = MaterialTheme.colors.codeBlockBackground,
        shape = MaterialTheme.shapes.small,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            modifier = Modifier.padding(16.dp),
            text = text,
            style = textStyle.merge(paragraphStyle)
        )
    }
}

/**
Markup(
MarkupType.Link,
54,
79,
"https://codelabs.developers.google.com/codelabs/on-demand-dynamic-delivery/index.html"
)
 */
fun paragraphToAnnotatedString(
    paragraph: Paragraph,
    typography: Typography,
    codeBlockBackground: Color
): AnnotatedString {
    val styles: List<AnnotatedString.Range<SpanStyle>> = paragraph.markups
        .map { it.toAnnotatedStringItem(typography, codeBlockBackground) }
    return AnnotatedString(text = paragraph.text, spanStyles = styles)
}

fun Markup.toAnnotatedStringItem(
    typography: Typography,
    codeBlockBackground: Color
): AnnotatedString.Range<SpanStyle> {
    return when (this.type) {
        MarkupType.Italic -> {
            AnnotatedString.Range(
                typography.body1.copy(fontStyle = FontStyle.Italic).toSpanStyle(),
                start,
                end
            )
        }
        MarkupType.Link -> {
            AnnotatedString.Range(
                typography.body1.copy(textDecoration = TextDecoration.Underline).toSpanStyle(),
                start,
                end
            )
        }
        MarkupType.Bold -> {
            AnnotatedString.Range(
                typography.body1.copy(fontWeight = FontWeight.Bold).toSpanStyle(),
                start,
                end
            )
        }
        MarkupType.Code -> {
            AnnotatedString.Range(
                typography.body1
                    .copy(
                        background = codeBlockBackground,
                        fontFamily = FontFamily.Monospace
                    ).toSpanStyle(),
                start,
                end
            )
        }
    }
}

//tag 属性动态化
@Composable
private fun ParagraphType.getTextAndParagraphStyle(): ParagraphStyling {
    val typography = MaterialTheme.typography
    var textStyle: TextStyle = typography.body1
    var paragraphStyle = ParagraphStyle()
    var trailingPadding = 24.dp

    when (this) {
        ParagraphType.Caption -> textStyle = typography.body1
        ParagraphType.Title -> textStyle = typography.h4
        ParagraphType.Subhead -> {
            textStyle = typography.h6
            trailingPadding = 16.dp
        }
        ParagraphType.Text -> {
            textStyle = typography.body1.copy(lineHeight = 28.sp)
            paragraphStyle = paragraphStyle.copy(lineHeight = 28.sp)
        }
        ParagraphType.Header -> {
            textStyle = typography.h5
            trailingPadding = 16.dp
        }
        ParagraphType.CodeBlock -> textStyle = typography.body1.copy(
            fontFamily = FontFamily.Monospace
        )
        ParagraphType.Quote -> textStyle = typography.body1
        ParagraphType.Bullet -> {
            paragraphStyle = ParagraphStyle(textIndent = TextIndent(firstLine = 8.sp))
        }
    }
    return ParagraphStyling(
        textStyle,
        paragraphStyle,
        trailingPadding
    )
}

@Composable
fun HeadTitle(post: Post) {
    Text(text = post.title, style = MaterialTheme.typography.h5)

    post.subtitle?.let { subtitle ->
        Text(
            text = subtitle,
            style = MaterialTheme.typography.subtitle2,
            modifier = Modifier.alpha(0.35f)
        )
    }
}


@Composable
fun PostHeaderImage(post: Post, modifier: Modifier = Modifier) {
    val imageModifier =
        modifier
            .heightIn(180.dp)
            .fillMaxWidth()
            .clip(shape = MaterialTheme.shapes.medium)
    Image(
        painter = painterResource(post.imageId),
        contentDescription = null, // decorative
        modifier = imageModifier,
        contentScale = ContentScale.Crop
    )
    Spacer(Modifier.height(defaultSpacerSize))
}

@Preview("Post content")
@Preview("Post content (dark)", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewPost() {
    Week4Theme() {
        Surface {
            PostContent(post = post3)
        }
    }
}
