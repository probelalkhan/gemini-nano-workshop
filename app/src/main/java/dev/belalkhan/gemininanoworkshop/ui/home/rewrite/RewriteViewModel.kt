package dev.belalkhan.gemininanoworkshop.ui.home.rewrite

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.genai.common.DownloadCallback
import com.google.mlkit.genai.common.FeatureStatus
import com.google.mlkit.genai.common.GenAiException
import com.google.mlkit.genai.rewriting.Rewriter
import com.google.mlkit.genai.rewriting.RewriterOptions
import com.google.mlkit.genai.rewriting.Rewriting
import com.google.mlkit.genai.rewriting.RewritingRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.belalkhan.gemininanoworkshop.bytesToMB
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RewriteState(
    val input: String = "The team needs to urgently fix this issue before the big product launch.",
    val messages: List<ChatMessage> = listOf(
        ChatMessage(
            "Hi there! Type a message and I'll give you some rewrite suggestions.",
            isFromUser = false
        )
    ),
    val suggestion: String = "",
    val isLoading: Boolean = false,
    val mbToDownload: Double = 0.0,
    val mbDownloaded: Double = 0.0,
    val errorMessage: String? = null,
    val selectedRewriteType: RewriteType = RewriteType.REPHRASE,
) {
    val downloadProgress: String
        get() = "Downloading $mbDownloaded of $mbToDownload MB..."
}

data class ChatMessage(
    val message: String,
    val isFromUser: Boolean = true
)

@OptIn(FlowPreview::class)
@HiltViewModel
class RewriteViewModel @Inject constructor(
    private val application: Application
) : ViewModel() {

    private val _state = MutableStateFlow(RewriteState())
    val state: StateFlow<RewriteState> = _state
    private var summarizationJob: Job? = null

    fun rewrite(rewriter: Rewriter) {
        summarizationJob?.cancel()

        val currentState = _state.value
        val text = currentState.input.trim()

        if (currentState.isLoading || text.isBlank()) {
            if (text.isBlank()) setError("Input is empty.")
            return
        }

        _state.update {
            it.copy(
                isLoading = true,
                suggestion = "",
                errorMessage = null,
                mbToDownload = 0.0,
                mbDownloaded = 0.0
            )
        }

        summarizationJob = viewModelScope.launch(Dispatchers.IO) {
            try {
                when (val status = rewriter.checkFeatureStatus().await()) {
                    FeatureStatus.UNAVAILABLE -> {
                        setError("Summarization feature is not available on this device.")
                    }

                    FeatureStatus.DOWNLOADABLE -> {
                        rewriter.downloadFeature(object : DownloadCallback {
                            override fun onDownloadStarted(bytesToDownload: Long) {
                                _state.update {
                                    it.copy(
                                        mbToDownload = bytesToDownload.bytesToMB(),
                                        mbDownloaded = 0.0
                                    )
                                }
                            }

                            override fun onDownloadProgress(totalBytesDownloaded: Long) {
                                _state.update {
                                    it.copy(mbDownloaded = totalBytesDownloaded.bytesToMB())
                                }
                            }

                            override fun onDownloadFailed(e: GenAiException) {
                                setError("Download failed: ${e.message}")
                            }

                            override fun onDownloadCompleted() {
                                startRewritingSuggestions(rewriter)
                            }
                        })
                    }

                    FeatureStatus.DOWNLOADING -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = "The summarization model is currently downloading. Please wait for it to finish."
                            )
                        }
                    }

                    FeatureStatus.AVAILABLE -> {
                        _state.update {
                            it.copy(mbToDownload = 0.0, mbDownloaded = 0.0)
                        }
                        startRewritingSuggestions(rewriter)
                    }

                }
            } catch (e: Exception) {
                setError("Unexpected error: ${e.message}")
            }
        }
    }

    fun onRewriteTypeSelected(rewriteType: RewriteType) {
        summarizationJob?.cancel()
        _state.update { it.copy(selectedRewriteType = rewriteType) }
        val rewriterOptions = RewriterOptions.builder(application) // Use the injected context
            .setOutputType(rewriteType.value)
            .setLanguage(RewriterOptions.Language.ENGLISH)
            .build()
        rewrite(Rewriting.getClient(rewriterOptions))
    }

    fun onInputChange(input: String) {
        _state.update { it.copy(input = input) }
    }

    private fun startRewritingSuggestions(rewriter: Rewriter) {
        val request = RewritingRequest.builder(state.value.input).build()
        rewriter.runInference(request) { newToken ->
            _state.update { it.copy(isLoading = false, suggestion = it.suggestion + newToken) }
        }
    }

    fun onSend(message: String) {
        if (message.isNotBlank()) {
            _state.update {
                it.copy(messages = it.messages + ChatMessage(message))
            }
        }
    }

    private fun setError(message: String) {
        _state.update { it.copy(isLoading = false, errorMessage = message) }
    }
}