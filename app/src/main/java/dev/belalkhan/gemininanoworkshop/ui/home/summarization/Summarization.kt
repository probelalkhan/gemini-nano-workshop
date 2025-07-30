package dev.belalkhan.gemininanoworkshop.ui.home.summarization

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.Clipboard
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.belalkhan.gemininanoworkshop.ui.theme.AppTheme
import dev.belalkhan.gemininanoworkshop.ui.theme.DayNightPreview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummarizationScreen(
    onBack: () -> Unit,
    viewModel: SummarizationViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsStateWithLifecycle()
    val clipboard: Clipboard = LocalClipboard.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    if (state.value.summary.isNotBlank()) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.clearSummary() },
            sheetState = sheetState,
            dragHandle = null,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = "Summary",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Text(
                    text = state.value.summary,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = {
                        // Clipboard copy logic placeholder
                    }) {
                        Text("Copy")
                    }
                    Button(onClick = viewModel::clearSummary) {
                        Text("Close")
                    }
                }
            }
        }
    }

    Summarization(
        modifier = Modifier.padding(16.dp),
        input = state.value.input,
        errorMessage = state.value.errorMessage,
        isSummarizing = state.value.isLoading,
        downloadProgress = if (state.value.isLoading && state.value.mbToDownload > 0)
            state.value.downloadProgress else null,
        onBack = onBack,
        onInputValueChange = viewModel::onInputChange,
        onPasteFromClipboard = {
            val clip = clipboard.nativeClipboard.primaryClip
            val firstItem = clip?.getItemAt(0)
            val text = firstItem?.text?.toString() ?: ""
            viewModel.onInputChange(text)
        },
        onSummarize = viewModel::summarize,
        actionButtonLabel = state.value.actionButtonLabel
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
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Text Summarizer", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                windowInsets = WindowInsets.statusBars
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                "Enter or paste a long paragraph below:",
                style = MaterialTheme.typography.bodyLarge
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                OutlinedTextField(
                    value = input,
                    onValueChange = onInputValueChange,
                    placeholder = { Text("Paste or type a paragraph here...") },
                    modifier = Modifier
                        .fillMaxSize(),
                    shape = RoundedCornerShape(14.dp),
                    textStyle = MaterialTheme.typography.bodyLarge,
                    isError = errorMessage != null,
                    singleLine = false,
                )

                if (input.isNotBlank()) {
                    IconButton(
                        onClick = { onInputValueChange("") },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear input"
                        )
                    }
                }
            }

            if (!errorMessage.isNullOrBlank()) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            if (!downloadProgress.isNullOrBlank()) {
                Text(
                    text = downloadProgress,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = onPasteFromClipboard) {
                    Text("Paste from Clipboard")
                }

                Button(
                    onClick = onSummarize,
                    enabled = input.isNotBlank() && !isSummarizing
                ) {
                    Text(actionButtonLabel)
                }
            }
        }
    }
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