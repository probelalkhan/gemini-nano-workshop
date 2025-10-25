package dev.belalkhan.gemininanoworkshop.ui.home.proofread

import android.content.Context
import com.google.mlkit.genai.proofreading.Proofreader
import com.google.mlkit.genai.proofreading.ProofreaderOptions
import com.google.mlkit.genai.proofreading.Proofreading
import com.google.mlkit.genai.rewriting.RewriterOptions
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext

@InstallIn(ViewModelComponent::class)
@Module
object ProofReaderModule {
    @Provides
    fun getProofReader(@ApplicationContext context: Context): Proofreader {
        val proofReadingOption = ProofreaderOptions.builder(context)
            .setInputType(ProofreaderOptions.InputType.KEYBOARD)
            .setLanguage(RewriterOptions.Language.ENGLISH)
            .build()
        return Proofreading.getClient(proofReadingOption)
    }
}
