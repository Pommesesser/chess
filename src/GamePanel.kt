import java.awt.Color
import java.awt.Graphics
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JPanel

const val tileSize = DIM / 8

class GamePanel: JPanel() {
    var state = initialGameState()
    var selection: Pair<Int, Int>? = null

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
        selection?.let {
            g.color = Color(0, 255, 0, 100)
            g.fillRect(it.second * tileSize, it.first * tileSize, tileSize, tileSize)
        }
    }

    fun highlightLegalMoves(g: Graphics) {
        selection?.let {
            val legalMoves = state.legalMoves(it)

            g.color = Color(255, 0, 0, 100)
            legalMoves.forEach { move ->
                g.fillRect(move.second * tileSize, move.first * tileSize, tileSize, tileSize)
            }
        }
    }

    fun renderPieces(g: Graphics) {
        for (r in 0..7) {
            for (c in 0..7) {
                val piece = state.data[r][c]

                piece?.let {
                    g.color = if (it.color == PieceColor.WHITE)
                        Color.WHITE
                    else
                        Color.BLACK
                    g.drawString(it.type.name.first().toString(), c * tileSize + tileSize / 3, r * tileSize + tileSize * 2 / 3)
                }
            }
        }
    }

    fun handleClick(r: Int, c: Int) {
        if (selection == null) {
            selection = Pair(r,c)
            repaint()
            return
        }

        val oldSelection = selection!!
        val newSelection = r to c
        val oldSelectionLegalMoves = state.legalMoves(oldSelection)

        if (oldSelectionLegalMoves.contains(newSelection)) {
            state.move()
            selection = null
            repaint()
        } else {
            selection = newSelection
            repaint()
        }
    }
}