package dev.joewilliams.logatro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
    Column(modifier = modifier) {
        Row {
            Text(text = "Score: $score")
        }
        Row {
            for (tile in selectedTiles) {
                RackTile(tile = tile) {

                }
            }
        }
        Row {
            for(tile in rackTiles) {
                RackTile(tile = tile) {
                    viewModel.tileTapped(tile)
                }
            }
        }
        Button(onClick = { viewModel.scoreWord(selectedTiles) }) {
            Text(text = "Submit")
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
        .size(60.dp)) {
        Text(modifier = Modifier.align(Alignment.Center), text = tile.letter.toString(), style = Typography.titleLarge)
    }
}