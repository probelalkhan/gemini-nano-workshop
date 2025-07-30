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
