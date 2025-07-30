package dev.belalkhan.gemininanoworkshop.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import dev.belalkhan.gemininanoworkshop.ui.nav.Feature
import dev.belalkhan.gemininanoworkshop.ui.theme.AppTheme
import dev.belalkhan.gemininanoworkshop.ui.theme.DayNightPreview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onNavigate: (Feature) -> Unit) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentWindowInsets = WindowInsets.systemBars
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)         // Your own content spacing
        ) {
            Home { onNavigate(it) }
        }
    }
}


@Composable
fun Home(onNavigate: (Feature) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            "Gemini Nano GenAI Demos",
            style = MaterialTheme.typography.displayMedium,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(Feature.featureScreens) { feature ->
                FeatureCard(feature = feature, onClick = { onNavigate(feature) })
            }
        }
    }
}

@Composable
fun FeatureCard(feature: Feature, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(vertical = 4.dp)
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(feature.iconRes),
                contentDescription = feature.title,
                modifier = Modifier.size(64.dp),
                tint = Color.Unspecified
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(feature.title, style = MaterialTheme.typography.headlineSmall)
                Text(
                    feature.subtitle,
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.Gray
                )
            }
        }
    }
}


@DayNightPreview
@Composable
private fun HomeScreenPreview() {
    AppTheme {
        HomeScreen(onNavigate = {})
    }
}