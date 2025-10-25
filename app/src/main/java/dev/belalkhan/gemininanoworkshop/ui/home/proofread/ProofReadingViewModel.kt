package dev.belalkhan.gemininanoworkshop.ui.home.proofread

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.genai.common.DownloadCallback
import com.google.mlkit.genai.common.FeatureStatus
import com.google.mlkit.genai.common.GenAiException
import com.google.mlkit.genai.proofreading.Proofreader
import com.google.mlkit.genai.proofreading.ProofreadingRequest
import com.google.mlkit.genai.rewriting.Rewriter
import com.google.mlkit.genai.rewriting.RewritingRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.belalkhan.gemininanoworkshop.bytesToMB
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProofReadingState(
    val input: String = "the teem nids to urgntly fx dis isu bfor d big product lanch.",
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
class ProofReadingViewModel @Inject constructor(
    private val proofReader: Proofreader
) : ViewModel() {

    private val _state = MutableStateFlow(ProofReadingState())
    val state: StateFlow<ProofReadingState> = _state
    private var proofreadingJob: Job? = null

    private val _inputFlow = MutableStateFlow("")

    init {
        viewModelScope.launch {
            _inputFlow
                .debounce(500)
                .distinctUntilChanged()
                .filter { input -> input.length > 5 }
                .collectLatest { proofread() }
        }
    }

    private fun proofread() {
        proofreadingJob?.cancel()

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

        proofreadingJob = viewModelScope.launch(Dispatchers.IO) {
            try {
                when (val status = proofReader.checkFeatureStatus().await()) {
                    FeatureStatus.UNAVAILABLE -> {
                        setError("Summarization feature is not available on this device.")
                    }

                    FeatureStatus.DOWNLOADABLE -> {
                        proofReader.downloadFeature(object : DownloadCallback {
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
                                startProofReading()
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
                        startProofReading()
                    }

                }
            } catch (e: Exception) {
                setError("Unexpected error: ${e.message}")
            }
        }
    }

    fun onInputChange(input: String) {
        _state.update { it.copy(input = input) }
        _inputFlow.value = input
    }

    private fun startProofReading() {
        val request = ProofreadingRequest.builder(state.value.input).build()
        proofReader.runInference(request) { newToken ->
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
