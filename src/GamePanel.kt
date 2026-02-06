import java.awt.Color
import java.awt.Graphics
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JPanel

const val tileSize = DIM / 8

class GamePanel: JPanel() {
    var state = initialState()
    var selectedTile: Tile? = null

    init {
        addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                val r = (e.y / tileSize).coerceIn(0, 7)
                val c = (e.x / tileSize).coerceIn(0, 7)
                handleClick(r, c)
            }
        })
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        renderGrid(g)
        highlightSelected(g)
        highlightLegalMoves(g)
        renderPieces(g)
    }

    fun renderGrid(g: Graphics) {
        for (r in 0..7) {
            for (c in 0..7) {
                val isDark = (r + c) % 2 == 1
                g.color = if
                                  (isDark) Color.DARK_GRAY
                else
                    Color.LIGHT_GRAY
                g.fillRect(c * tileSize, r * tileSize, tileSize, tileSize)
            }
        }
    }

    fun highlightSelected(g: Graphics) {
        selectedTile?.let {
            g.color = Color(0, 255, 0, 100)
            g.fillRect(it.c * tileSize, it.r * tileSize, tileSize, tileSize)
        }
    }

    fun highlightLegalMoves(g: Graphics) {
        selectedTile?.let { selection ->
            state.tile(selection.r, selection.c)?.let { tile ->
                val legalMoves = state.legalMoves(tile)

                g.color = Color(255, 0, 0, 100)
                legalMoves.forEach { tile ->
                    g.fillRect(tile.c * tileSize, tile.r * tileSize, tileSize, tileSize)
                }
            }
        }
    }

    fun renderPieces(g: Graphics) {
        state.tiles.forEach { tile ->
            tile.p?.let { piece ->
                g.color = if (piece.color == PieceColor.WHITE)
                    Color.WHITE
                else
                    Color.BLACK
                g.drawString(piece.type.name.first().toString(), tile.c * tileSize + tileSize / 3, tile.r * tileSize + tileSize * 2 / 3)
            }
        }
    }

    fun handleClick(r: Int, c: Int) {
        selectedTile ?: run {
            selectedTile = state.tile(r, c)
            repaint()
            return
        }
        selectedTile?.let { oldSelection ->
            val newTile = state.tile(r, c)!!
            val legalTiles = state.legalMoves(oldSelection)

            if (legalTiles.contains(newTile)) {
                state.move(oldSelection, newTile)
                selectedTile = null
                repaint()
            }
            else {
                selectedTile = state.tile(r, c)
                repaint()
            }
        }
    }
}