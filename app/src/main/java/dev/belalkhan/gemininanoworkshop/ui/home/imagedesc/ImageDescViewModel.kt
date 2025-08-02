package dev.belalkhan.gemininanoworkshop.ui.home.imagedesc

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.genai.common.DownloadCallback
import com.google.mlkit.genai.common.FeatureStatus
import com.google.mlkit.genai.common.GenAiException
import com.google.mlkit.genai.imagedescription.ImageDescriber
import com.google.mlkit.genai.imagedescription.ImageDescriptionRequest
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

data class ImageDescState(
    val description: String = "",
    val isLoading: Boolean = false,
    val mbToDownload: Double = 0.0,
    val mbDownloaded: Double = 0.0,
    val errorMessage: String? = null
) {
    val downloadProgress: String
        get() = "Downloading $mbDownloaded of $mbToDownload MB..."
}

@HiltViewModel
class ImageDescViewModel @Inject constructor(
    private val imageDescriber: ImageDescriber
) : ViewModel() {

    private val _state = MutableStateFlow(ImageDescState())
    val state: StateFlow<ImageDescState> = _state

    private var imageDescJob: Job? = null

    fun describe(bitmap: Bitmap) {
        imageDescJob?.cancel()

        _state.update {
            it.copy(
                isLoading = true,
                description = "",
                errorMessage = null,
                mbToDownload = 0.0,
                mbDownloaded = 0.0
            )
        }

        imageDescJob = viewModelScope.launch(Dispatchers.IO) {
            try {
                when (val status = imageDescriber.checkFeatureStatus().await()) {
                    FeatureStatus.UNAVAILABLE -> {
                        setError("Image Description is not available on this device.")
                    }

                    FeatureStatus.DOWNLOADABLE -> {
                        imageDescriber.downloadFeature(object : DownloadCallback {
                            override fun onDownloadStarted(bytesToDownload: Long) {
                                _state.update {
                                    it.copy(mbToDownload = bytesToDownload.bytesToMB())
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
                                startDescription(bitmap)
                            }
                        })
                    }

                    FeatureStatus.DOWNLOADING -> {
                        setError("Model is already downloading. Please wait.")
                    }

                    FeatureStatus.AVAILABLE -> {
                        _state.update {
                            it.copy(mbToDownload = 0.0, mbDownloaded = 0.0)
                        }
                        startDescription(bitmap)
                    }
                }
            } catch (e: Exception) {
                setError("Unexpected error: ${e.message}")
            }
        }
    }

    private fun startDescription(bitmap: Bitmap) {
        val request = ImageDescriptionRequest.builder(bitmap).build()
        imageDescriber.runInference(request) { token ->
            _state.update { it.copy(description = it.description + token) }
        }
    }

    fun clearDescription() {
        _state.update {
            it.copy(description = "", errorMessage = null, isLoading = false)
        }
    }

    private fun setError(message: String) {
        _state.update { it.copy(isLoading = false, errorMessage = message) }
    }

    override fun onCleared() {
        imageDescJob?.cancel()
        imageDescriber.close()
        super.onCleared()
    }
}
