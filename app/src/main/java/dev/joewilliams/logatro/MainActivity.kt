package dev.joewilliams.logatro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.joewilliams.logatro.model.Tile
import dev.joewilliams.logatro.ui.theme.LogatroTheme
import dev.joewilliams.logatro.ui.theme.Typography

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            LogatroTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PlayingArea(modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize(), viewModel = MainViewModel(context.resources))
                }
            }
        }
    }
}

@Composable
fun PlayingArea(modifier: Modifier = Modifier, viewModel: MainViewModel) {
    val score by viewModel.scoreState.collectAsState()
    val selectedTiles by viewModel.selectedTilesState.collectAsState()
    val rackTiles by viewModel.tileRackState.collectAsState()
    val wordlessState by viewModel.wordlessState.collectAsState()
    Column(modifier = modifier) {
        Row {
            Text(text = "Score: $score")
        }
        Row(modifier = Modifier.height(80.dp).padding(8.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            for (tile in selectedTiles) {
                RackTile(tile = tile) {
                    viewModel.tileTapped(tile)
                }
            }
        }
        Row(modifier = Modifier.padding(8.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            for(tile in rackTiles) {
                RackTile(tile = tile) {
                    viewModel.tileTapped(tile)
                }
            }
        }
        Button(enabled = !wordlessState, onClick = { viewModel.scoreWord(selectedTiles) }) {
            Text(text = "Submit")
        }
        Button(enabled = selectedTiles.isNotEmpty(), onClick = { viewModel.discard(selectedTiles) }) {
            Text(text = "Discard")
        }
        Row {
            Button(enabled = selectedTiles.isNotEmpty(), onClick = { viewModel.reset() }) {
                Text(text = "Reset")
            }

            Button(onClick = { viewModel.shuffle() }) {
                Text(text = "Shuffle")
            }
        }
        if (wordlessState) {
            Text(text = "No words are possible. Please discard some tiles.")
        }
    }
}

@Composable
fun RackTile(
    modifier: Modifier = Modifier,
    tile: Tile,
    onTap: () -> Unit
) {
    Box(modifier = modifier
        .clickable { onTap.invoke() }
        .background(Color.Blue, shape = RoundedCornerShape(12.dp))
        .size(50.dp)) {
        Text(modifier = Modifier.align(Alignment.Center), text = tile.letter.toString(), style = Typography.titleLarge)
    }
}