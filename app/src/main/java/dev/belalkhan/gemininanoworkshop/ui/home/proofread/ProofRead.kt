package dev.belalkhan.gemininanoworkshop.ui.home.proofread

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.sharp.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.belalkhan.gemininanoworkshop.ui.theme.AppTheme
import dev.belalkhan.gemininanoworkshop.ui.theme.DayNightPreview

@Composable
fun ProofReadingScreen(
    onBack: () -> Unit, viewModel: ProofReadingViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ProofReadScreenContent(
        state = state,
        onTextFieldValueChange = viewModel::onInputChange,
        onSendClick = { viewModel.onSend(state.input) },
        onSuggestionClick = { suggestion -> viewModel.onSend(suggestion) },
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProofReadScreenContent(
    state: ProofReadingState,
    onTextFieldValueChange: (String) -> Unit,
    onSendClick: () -> Unit,
    onSuggestionClick: (String) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Rewrite Assistant") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                reverseLayout = true,
                verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.Bottom)
            ) {
                items(state.messages.reversed()) { chat ->
                    MessageBubble(chatMessage = chat)
                }
            }

            SuggestionsArea(
                state = state,
                onSuggestionClick = onSuggestionClick,
            )

            MessageInput(
                value = state.input,
                onValueChange = onTextFieldValueChange,
                onSend = onSendClick
            )
        }
    }
}

@Composable
fun SuggestionsArea(
    state: ProofReadingState,
    onSuggestionClick: (String) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            when {
                state.isLoading -> {
                    if (state.mbToDownload > 0) {
                        Text(
                            text = state.downloadProgress,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        CircularProgressIndicator(modifier = Modifier.size(32.dp))
                    }
                }

                state.errorMessage != null -> {
                    Text(
                        text = state.errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 14.sp
                    )
                }

                state.suggestion.isNotBlank() -> {
                    SuggestionBubble(
                        suggestion = state.suggestion.trim(),
                        onClick = { onSuggestionClick(state.suggestion) }
                    )
                }
            }
        }
    }
}

@Composable
fun SuggestionBubble(suggestion: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = suggestion,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun MessageBubble(chatMessage: ChatMessage) {
    val bubbleColor =
        if (chatMessage.isFromUser) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = if (chatMessage.isFromUser) 64.dp else 0.dp,
                end = if (chatMessage.isFromUser) 0.dp else 64.dp
            ),
        contentAlignment = if (chatMessage.isFromUser) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(bubbleColor)
                .padding(12.dp)
        ) {
            Text(
                text = chatMessage.message, color = contentColorFor(bubbleColor), fontSize = 16.sp
            )
        }
    }
}

@Composable
fun MessageInput(
    value: String, onValueChange: (String) -> Unit, onSend: () -> Unit
) {
    Surface(
        tonalElevation = 4.dp, modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = { Text("Type a message...") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(24.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = { onSend() })
            )
            Spacer(modifier = Modifier.width(8.dp))
            FloatingActionButton(
                onClick = onSend,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 0.dp)
            ) {
                Icon(Icons.AutoMirrored.Sharp.Send, contentDescription = "Send")
            }
        }
    }
}

@DayNightPreview
@Composable
fun ProofReadingScreenPreview() {
    AppTheme {
        ProofReadScreenContent(
            state = ProofReadingState(
                messages = listOf(
                    ChatMessage(
                        "Hi there! Type a message and I'll give you some rewrite suggestions.",
                        isFromUser = false
                    ), ChatMessage("Hello, how is it going today?", isFromUser = true)
                ),
                suggestion = "How about we prioritize fixing this before the launch?",
                input = "The team needs to urgently fix this issue before the big product launch.",
            ),
            onTextFieldValueChange = {},
            onSendClick = {},
            onSuggestionClick = {},
            onBack = {})
    }
}
