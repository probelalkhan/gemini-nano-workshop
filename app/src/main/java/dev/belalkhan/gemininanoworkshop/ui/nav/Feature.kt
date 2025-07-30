package dev.belalkhan.gemininanoworkshop.ui.nav

import androidx.annotation.DrawableRes
import dev.belalkhan.gemininanoworkshop.R

sealed class Feature(
    val route: String,
    val title: String,
    val subtitle: String,
    @param:DrawableRes val iconRes: Int
) {
    object Summarization : Feature(
        "summarization",
        "Summarization",
        "Summarize articles or chats",
        R.drawable.ic_summarize
    )

    object Proofreading : Feature(
        "proofreading",
        "Proofreading",
        "Fix typos in short messages",
        R.drawable.ic_proofread
    )

    object Rewrite : Feature(
        "rewrite",
        "Rewrite",
        "Change tone or style of messages",
        R.drawable.ic_rewrite
    )

    object ImageDescription : Feature(
        "image_description",
        "Image Description",
        "Generate alt text for an image",
        R.drawable.ic_image
    )

    companion object {
        val featureScreens: List<Feature>
            get() = listOf(
                Summarization,
                Proofreading,
                Rewrite,
                ImageDescription
            )
    }
}