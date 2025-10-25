package dev.belalkhan.gemininanoworkshop.ui.home.rewrite

import android.content.Context
import com.google.mlkit.genai.imagedescription.ImageDescriber
import com.google.mlkit.genai.imagedescription.ImageDescriberOptions
import com.google.mlkit.genai.imagedescription.ImageDescription
import com.google.mlkit.genai.rewriting.Rewriter
import com.google.mlkit.genai.rewriting.RewriterOptions
import com.google.mlkit.genai.rewriting.Rewriting
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
object RewriteModule {
    @Provides
    fun getRewriter(@ApplicationContext context: Context): Rewriter {
        val rewriterOptions = RewriterOptions.builder(context)
            .setOutputType(RewriterOptions.OutputType.ELABORATE)
            .setLanguage(RewriterOptions.Language.ENGLISH)
            .build()
        return Rewriting.getClient(rewriterOptions)
    }
}
