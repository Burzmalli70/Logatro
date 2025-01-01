package dev.joewilliams.logatro

import android.content.res.Resources
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.joewilliams.logatro.model.Tile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.time.Clock
import kotlin.random.Random

class MainViewModel(private val resources: Resources): ViewModel() {
    private val tileBag: MutableList<Tile> = mutableListOf()
    private val dictionary: MutableMap<Char,MutableMap<Char,MutableList<String>>> = mutableMapOf()
    private val drawnTiles: MutableList<Tile> = mutableListOf()
    private val mutableTileRack: MutableStateFlow<List<Tile>> = MutableStateFlow(emptyList())
    val tileRackState: StateFlow<List<Tile>> = mutableTileRack
    private val mutableSelectedTiles: MutableStateFlow<List<Tile>> = MutableStateFlow(emptyList())
    val selectedTilesState: StateFlow<List<Tile>> = mutableSelectedTiles
    private var rackSize = 7
    private val mutableScore: MutableStateFlow<Int> = MutableStateFlow(0)
    val scoreState: StateFlow<Int> = mutableScore
    private val random = Random(Clock.systemDefaultZone().millis())
    var ready = false

    init {
        viewModelScope.launch(Dispatchers.IO) {
            loadDictionary()
            loadTiles()
            drawTiles()
        }
    }

    private fun loadDictionary() {
        val istream = resources.openRawResource(R.raw.dictionary)
        val reader = BufferedReader(InputStreamReader(istream))
        var word: String? = ""
        var lastStartLetter = 'A'
        print("Now loading $lastStartLetter")
        do {
            word = reader.readLine()
            if (word == null || word.length < 3) continue
            if (word[0] != lastStartLetter) {
                lastStartLetter = word[0]
                print("Now loading ${word[0]}")
            }
            val subdictionary = dictionary[word[0]] ?: mutableMapOf()
            val sublist = subdictionary[word[1]] ?: mutableListOf()
            sublist.add(word)
            subdictionary[word[1]] = sublist
            dictionary[word[0]] = subdictionary
        } while (word != null)
        istream.close()
        ready = true
    }

    private fun loadTiles() {
        var currentId = 0
        for (baseTile in DEFAULT_TILES.keys) {
            for(i in 1 .. (DEFAULT_TILES[baseTile] ?: 0)) {
                tileBag.add(baseTile.copy(id = currentId))
                currentId++
            }
        }
    }

    private suspend fun drawTiles() {
        if (tileRackState.value.size >= rackSize) return
        var tile: Tile? = null
        do {
            tile = drawTile() ?: continue
            mutableTileRack.emit(tileRackState.value.plus(tile))
        } while(tile != null && tileRackState.value.size < rackSize)
    }

    private fun drawTile(): Tile? {
        val tile = tileBag.randomOrNull(random)
        if (tile != null) tileBag.remove(tile)
        return tile
    }

    fun scoreWord(tiles: List<Tile>) {
        if (!checkWord(tiles.joinToString(separator = "") { it.letter.toString() })) return
        viewModelScope.launch {
            var score = 0
            var mult = 1.0
            for (tile in tiles) {
                score += tile.value
                mult *= tile.multiplier
            }
            mutableScore.emit(scoreState.value + score)
            drawnTiles.addAll(tiles)
            mutableTileRack.emit(tileRackState.value.minus(tiles.toSet()))
            mutableSelectedTiles.emit(emptyList())
            drawTiles()
        }
    }

    fun tileTapped(tile: Tile) {
        viewModelScope.launch {
            if (selectedTilesState.value.contains(tile)) {
                mutableSelectedTiles.emit(selectedTilesState.value.minus(tile))
            } else {
                mutableSelectedTiles.emit(selectedTilesState.value.plus(tile))
            }
        }
    }

    fun checkWord(word: String): Boolean {
        if (word.length < 3) return false
        return dictionary[word[0]]?.get(word[1])?.contains(word) ?: false
    }
}

val DEFAULT_TILES: Map<Tile, Int> = mapOf (
    Tile(0, 'A', 1) to 16,
    Tile(0, 'B', 3) to 4,
    Tile(0, 'C', 3) to 6,
    Tile(0, 'D', 2) to 8,
    Tile(0, 'E', 1) to 24,
    Tile(0, 'F', 4) to 4,
    Tile(0, 'G', 2) to 5,
    Tile(0, 'H', 4) to 5,
    Tile(0, 'I', 1) to 13,
    Tile(0, 'J', 8) to 2,
    Tile(0, 'K', 5) to 2,
    Tile(0, 'L', 1) to 7,
    Tile(0, 'M', 3) to 6,
    Tile(0, 'N', 1) to 13,
    Tile(0, 'O', 1) to 15,
    Tile(0, 'P', 3) to 4,
    Tile(0, 'Q', 10) to 2,
    Tile(0, 'R', 1) to 13,
    Tile(0, 'S', 1) to 10,
    Tile(0, 'T', 1) to 15,
    Tile(0, 'U', 1) to 7,
    Tile(0, 'V', 4) to 3,
    Tile(0, 'W', 4) to 4,
    Tile(0, 'X', 8) to 2,
    Tile(0, 'Y', 4) to 4,
    Tile(0, 'Z', 10) to 2,
)