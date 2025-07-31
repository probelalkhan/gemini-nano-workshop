package dev.belalkhan.gemininanoworkshop.ui.home.imagedesc

import android.content.Context
import com.google.mlkit.genai.imagedescription.ImageDescriber
import com.google.mlkit.genai.imagedescription.ImageDescriberOptions
import com.google.mlkit.genai.imagedescription.ImageDescription
import com.google.mlkit.genai.summarization.Summarization
import com.google.mlkit.genai.summarization.Summarizer
import com.google.mlkit.genai.summarization.SummarizerOptions
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext

@InstallIn(ViewModelComponent::class)
@Module
object ImageDescModule {

    @Provides
    fun getImageDescriber(@ApplicationContext context: Context): ImageDescriber {
        val options = ImageDescriberOptions.builder(context).build()
        return ImageDescription.getClient(options)
    }
}
