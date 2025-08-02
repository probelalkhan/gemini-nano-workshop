package dev.belalkhan.gemininanoworkshop.ui.home.summarization

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummarizationScreen(
    onBack: () -> Unit,
    viewModel: SummarizationViewModel = hiltViewModel()
) {
    Text(
        modifier = Modifier.padding(vertical = 128.dp, horizontal = 16.dp),
        text = "Summarization Screen To-Be Completed on Workshop"
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Summarization(
    modifier: Modifier = Modifier,
    input: String,
    errorMessage: String?,
    actionButtonLabel: String,
    isSummarizing: Boolean,
    downloadProgress: String?,
    onBack: () -> Unit,
    onInputValueChange: (String) -> Unit,
    onPasteFromClipboard: () -> Unit,
    onSummarize: () -> Unit,
) {

}

@DayNightPreview
@Composable
private fun SummarizationScreenPreview() {
    AppTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Summarization(
                modifier = Modifier.padding(16.dp),
                input = "This is a long paragraph for preview...",
                errorMessage = null,
                isSummarizing = false,
                downloadProgress = "Downloading 3.2 of 5.0 MB...",
                onBack = {},
                onInputValueChange = {},
                onPasteFromClipboard = {},
                onSummarize = {},
                actionButtonLabel = "Summarize"
            )
        }
    }
}
