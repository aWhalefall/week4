package com.example.week4.model

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.example.week4.data.interests.impl.*

/**
 * author：yangweichao@reworldgame.com
 * data: 2022/4/6 18:32
 * 数据模拟
 */
/**
 * Provides sample [Post] instances for Composable Previews.
 *
 * When creating a Composable Preview using @Preview, you can pass sample data
 * by annotating a parameter with @PreviewParameter:
 *
 * ```
 * @Preview
 * @Composable
 * fun MyPreview(@PreviewParameter(PostPreviewParameterProvider::class, limit = 2) post: Post) {
 *   MyComposable(post)
 * }
 * ```
 *
 * In this simple app we just return the hard-coded posts. When the app
 * would be more complex - e.g. retrieving the posts from a server - this would
 * be the right place to instantiate dummy instances.
 */
class PostPreviewParameterProvider : PreviewParameterProvider<Post> {
    override val values= sequenceOf(
      post1, post2, post3, post4,post5
    )


}