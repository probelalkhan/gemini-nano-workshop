package dev.belalkhan.gemininanoworkshop.ui.home.summarization

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.genai.common.DownloadCallback
import com.google.mlkit.genai.common.FeatureStatus
import com.google.mlkit.genai.common.GenAiException
import com.google.mlkit.genai.summarization.SummarizationRequest
import com.google.mlkit.genai.summarization.Summarizer
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.belalkhan.gemininanoworkshop.bytesToMB
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SummarizationState(
    val input: String = "",
    val summary: String = "",
    val isLoading: Boolean = false,
    val mbToDownload: Double = 0.0,
    val mbDownloaded: Double = 0.0,
    val errorMessage: String? = null
) {
    val downloadProgress: String
        get() = "Downloading $mbDownloaded of $mbToDownload MB..."

    val actionButtonLabel: String
        get() = when {
            mbToDownload > 0.0 && mbDownloaded < mbToDownload -> "Downloading..."
            isLoading -> "Summarizing..."
            else -> "Summarize"
        }
}

@HiltViewModel
class SummarizationViewModel @Inject constructor(
    private val summarizer: Summarizer
) : ViewModel() {

    private val _state = MutableStateFlow(SummarizationState())
    val state: StateFlow<SummarizationState> = _state
    private var summarizationJob: Job? = null

    fun summarize() {
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
                summary = "",
                errorMessage = null,
                mbToDownload = 0.0,
                mbDownloaded = 0.0
            )
        }

        summarizationJob = viewModelScope.launch(Dispatchers.IO) {
            try {
                when (val status = summarizer.checkFeatureStatus().await()) {
                    FeatureStatus.UNAVAILABLE -> {
                        setError("Summarization feature is not available on this device.")
                    }

                    FeatureStatus.DOWNLOADABLE -> {
                        summarizer.downloadFeature(object : DownloadCallback {
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
                                startStreamingSummarization(text)
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
                        startStreamingSummarization(text)
                    }

                }
            } catch (e: Exception) {
                setError("Unexpected error: ${e.message}")
            }
        }
    }

    private fun startStreamingSummarization(text: String) {
        val request = SummarizationRequest.builder(text).build()
        summarizer.runInference(request) { newToken ->
            _state.update { it.copy(summary = it.summary + newToken) }
        }
    }

    fun onInputChange(input: String) {
        _state.update { it.copy(input = input) }
    }

    fun clearSummary() {
        _state.update {
            it.copy(summary = "", errorMessage = null, isLoading = false)
        }
    }

    private fun setError(message: String) {
        _state.update { it.copy(isLoading = false, errorMessage = message) }
    }

    override fun onCleared() {
        summarizationJob?.cancel()
        summarizer.close()
        super.onCleared()
    }
}