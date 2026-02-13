import java.awt.BasicStroke
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JPanel

const val tileSize = DIM / 8

class Panel: JPanel() {
    var state = State.initial()
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
        renderSelected(g)
        renderLegalMoves(g)
        renderLastMove(g)

        renderCheck(g)
        renderPieces(g)
    }

    fun renderGrid(g: Graphics) {
        val light = Color(240, 217, 181)
        val dark  = Color(176, 99, 0)

        for (r in 0..7) {
            for (c in 0..7) {
                val isDark = (r + c) % 2 == 1
                g.color = if (isDark) dark else light
                g.fillRect(c * tileSize, r * tileSize, tileSize, tileSize)
            }
        }
    }

    fun renderSelected(g: Graphics) {
        g.color = Color(0, 255, 0, 100)

        selectedTile?.let {
            g.fillRect(it.c * tileSize, it.r * tileSize, tileSize, tileSize)
        }
    }

    fun renderLegalMoves(g: Graphics) {
        g.color = Color(55, 55, 55, 100)

        selectedTile?.let { selection ->
            state.tile(selection.r, selection.c)?.let { tile ->
                state.legalTiles(tile).forEach { tile ->
                    if (tile.p == null) {
                        val diameter = 15
                        val x = tile.c * tileSize + (tileSize - diameter) / 2
                        val y = tile.r * tileSize + (tileSize - diameter) / 2
                        g.fillOval(x, y, diameter, diameter)
                    } else {
                        val diameter = 40
                        val g2 = g as Graphics2D
                        val oldStroke = g2.stroke
                        val oldColor = g2.color
                        g2.color = g.color
                        g2.stroke = BasicStroke(7.toFloat())

                        val x = tile.c * tileSize + (tileSize - diameter) / 2
                        val y = tile.r * tileSize + (tileSize - diameter) / 2
                        g2.drawOval(x, y, diameter, diameter)

                        g2.stroke = oldStroke
                        g2.color = oldColor
                    }
                }
            }
        }
    }

    fun renderLastMove(g: Graphics) {
        g.color = Color(0, 255, 0, 100)

        state.lastMove?.let {
            g.fillRect(it.first.c * tileSize, it.first.r * tileSize, tileSize, tileSize)
            g.fillRect(it.second.c * tileSize, it.second.r * tileSize, tileSize, tileSize)
        }
    }

    fun renderCheck(g: Graphics) {
        g.color = Color(255, 0, 100)

        if (state.kingIsInCheck(PieceColor.WHITE)) {
            val king = state.king(PieceColor.WHITE)
            g.fillRect(
                king.c * tileSize,
                king.r * tileSize,
                tileSize,
                tileSize
            )
        } else if (state.kingIsInCheck(PieceColor.BLACK)) {
            val king = state.king(PieceColor.BLACK)
            g.fillRect(
                king.c * tileSize,
                king.r * tileSize,
                tileSize,
                tileSize
            )
        }
    }

    fun renderPieces(g: Graphics) {
        state.tiles.forEach { tile ->
            tile.p?.let { piece ->
                g.color = if (piece.color == PieceColor.WHITE)
                    Color.WHITE
                else
                    Color.BLACK
                g.drawString(
                    piece.type.name.first().toString(),
                    tile.c * tileSize + tileSize / 3,
                    tile.r * tileSize + tileSize * 2 / 3
                )
            }
        }
    }

    fun handleClick(r: Int, c: Int) {
        selectedTile ?: run {
            selectedTile = state.tile(r, c)
            repaint()
            return
        }
        selectedTile?.let { oldTile ->
            val newTile = state.tile(r, c)!!
            if (oldTile.r == newTile.r && oldTile.c == newTile.c) {
                selectedTile = null
                repaint()
                return
            }

            val legalTiles = state.legalTiles(oldTile)
            if (legalTiles.contains(newTile)) {
                state = state.move(oldTile, newTile)
                selectedTile = null
                repaint()
            } else {
                selectedTile = state.tile(r, c)
                repaint()
            }
        }
    }
}