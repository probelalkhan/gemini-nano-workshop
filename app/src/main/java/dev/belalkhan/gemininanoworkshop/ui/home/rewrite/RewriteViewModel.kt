package dev.belalkhan.gemininanoworkshop.ui.home.rewrite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.genai.common.DownloadCallback
import com.google.mlkit.genai.common.FeatureStatus
import com.google.mlkit.genai.common.GenAiException
import com.google.mlkit.genai.rewriting.Rewriter
import com.google.mlkit.genai.rewriting.RewritingRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.belalkhan.gemininanoworkshop.bytesToMB
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RewriteState(
    val input: String = "",
    val messages: List<ChatMessage> = listOf(
        ChatMessage(
            "Hi there! Type a message and I'll give you some rewrite suggestions.",
            isFromUser = false
        )
    ),
    val suggestions: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val mbToDownload: Double = 0.0,
    val mbDownloaded: Double = 0.0,
    val errorMessage: String? = null
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
    private val rewriter: Rewriter
) : ViewModel() {

    private val _state = MutableStateFlow(RewriteState())
    val state: StateFlow<RewriteState> = _state
    private var summarizationJob: Job? = null

    private val _inputFlow = MutableStateFlow("")

    init {
        _inputFlow
            .onEach { newValue ->
                _state.update { it.copy(input = newValue) }
            }
            .debounce(500L)
            .distinctUntilChanged()
            .filter { it.split(" ").filter(String::isNotBlank).size > 3 }
            .onEach { text ->
                rewrite()
            }
            .launchIn(viewModelScope)
    }


    fun rewrite() {
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
                suggestions = emptyList(),
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
                                startRewritingSuggestions(text)
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
                        startRewritingSuggestions(text)
                    }

                }
            } catch (e: Exception) {
                setError("Unexpected error: ${e.message}")
            }
        }
    }

    private fun startRewritingSuggestions(text: String) {
        val request = RewritingRequest.builder(text).build()
        rewriter.runInference(request) { newToken ->
            _state.update { it.copy(suggestions = it.suggestions + newToken) }
        }
    }

    fun onInputChange(input: String) {
        _inputFlow.value = input
    }

    fun clearSuggestions() {
        _state.update {
            it.copy(suggestions = emptyList(), errorMessage = null, isLoading = false)
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