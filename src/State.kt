data class State(
    val tiles: List<Tile>,
    val turn: PieceColor,
    val lastMove: Pair<Tile, Tile>?
) {
    companion object {
        fun initial(): State {
            val tiles = mutableListOf<Tile>()

            for (c in 0..7) {
                tiles.add(Tile(Piece(PieceType.PAWN, PieceColor.BLACK), 1, c))
                tiles.add(Tile(Piece(PieceType.PAWN, PieceColor.WHITE), 6, c))
            }

            tiles.add(Tile(Piece(PieceType.ROOK, PieceColor.BLACK), 0, 0))
            tiles.add(Tile(Piece(PieceType.ROOK, PieceColor.BLACK), 0, 7))
            tiles.add(Tile(Piece(PieceType.ROOK, PieceColor.WHITE), 7, 0))
            tiles.add(Tile(Piece(PieceType.ROOK, PieceColor.WHITE), 7, 7))

            tiles.add(Tile(Piece(PieceType.KNIGHT, PieceColor.BLACK), 0, 1))
            tiles.add(Tile(Piece(PieceType.KNIGHT, PieceColor.BLACK), 0, 6))
            tiles.add(Tile(Piece(PieceType.KNIGHT, PieceColor.WHITE), 7, 1))
            tiles.add(Tile(Piece(PieceType.KNIGHT, PieceColor.WHITE), 7, 6))

            tiles.add(Tile(Piece(PieceType.BISHOP, PieceColor.BLACK), 0, 2))
            tiles.add(Tile(Piece(PieceType.BISHOP, PieceColor.BLACK), 0, 5))
            tiles.add(Tile(Piece(PieceType.BISHOP, PieceColor.WHITE), 7, 2))
            tiles.add(Tile(Piece(PieceType.BISHOP, PieceColor.WHITE), 7, 5))

            tiles.add(Tile(Piece(PieceType.QUEEN, PieceColor.BLACK), 0, 3))
            tiles.add(Tile(Piece(PieceType.QUEEN, PieceColor.WHITE), 7, 3))

            tiles.add(Tile(Piece(PieceType.KING, PieceColor.BLACK), 0, 4))
            tiles.add(Tile(Piece(PieceType.KING, PieceColor.WHITE), 7, 4))

            for (r in 2..5) {
                for (c in 0..7)
                    tiles.add(Tile(null, r, c))
            }

            return State(tiles, PieceColor.WHITE, null)
        }
    }

    fun tile(r: Int, c: Int): Tile? {
        return tiles.find { it.r == r && it.c == c }
    }

    private fun pieces(color: PieceColor): List<Tile> {
        return tiles.filter { it.p != null }
            .filter { it.p?.color == color }
    }

    fun king(color: PieceColor): Tile {
        return pieces(color).find { it.p?.type == PieceType.KING }!!
    }

    fun kingIsInCheck(color: PieceColor): Boolean =
        threatenedTilesByColor(opponentColor(color)).contains(king(color))

    private fun opponentColor(color: PieceColor): PieceColor =
        if (color == PieceColor.WHITE) PieceColor.BLACK else PieceColor.WHITE

    private fun movePutsKingInCheck(from: Tile, to: Tile): Boolean {
        val newState = simulateMove(from, to)
        return newState.kingIsInCheck(from.p!!.color)
    }

    fun hasLegalMoves(): Boolean {
        return pieces(turn).any { tile -> legalTiles(tile).isNotEmpty() }
    }

    fun isCheckmate(): Boolean =
        kingIsInCheck(turn) && !hasLegalMoves()

    fun isStalemate(): Boolean =
        !kingIsInCheck(turn) && !hasLegalMoves()

    fun isGameOver(): Boolean =
        !hasLegalMoves()

    private fun simulateMove(from: Tile, to: Tile): State {
        val newTiles = tiles.map { tile ->
            when {
                tile.r == from.r && tile.c == from.c -> Tile(null, tile.r, tile.c)
                tile.r == to.r && tile.c == to.c -> Tile(from.p, tile.r, tile.c)
                else -> tile
            }
        }
        return State(newTiles, turn, from to to)
    }

    fun move(from: Tile, to: Tile): State {
        val nextState = simulateMove(from, to)
        return nextState.copy(turn = opponentColor(turn))
    }

    fun legalTiles(tile: Tile): List<Tile> {
        if (tile.p?.color != turn)
            return emptyList()

        val baseMoves = when (tile.p.type) {
            PieceType.PAWN -> pawnLegalTiles(tile)
            else -> threatenedTiles(tile)
        }
        return baseMoves.filter { !movePutsKingInCheck(tile, it) }
    }

    private fun threatenedTiles(tile: Tile): List<Tile> {
        return tile.p?.let {
            when (it.type) {
                PieceType.PAWN -> pawnThreateningTiles(tile)
                PieceType.KNIGHT -> knightThreateningTiles(tile)
                PieceType.ROOK -> rookThreateningTiles(tile)
                PieceType.BISHOP -> bishopThreateningTiles(tile)
                PieceType.QUEEN -> queenThreateningTiles(tile)
                PieceType.KING -> kingThreateningTiles(tile)
            }
        } ?: emptyList()
    }

    private fun threatenedTilesByColor(threateningColor: PieceColor): List<Tile> =
        pieces(threateningColor).flatMap { threatenedTiles(it) }

    private fun pawnLegalTiles(tile: Tile): List<Tile> {
        val moves = mutableListOf<Tile>()
        val dir = if (tile.p!!.color == PieceColor.WHITE) -1 else 1

        tile(tile.r + dir, tile.c)?.let { if (it.p == null) moves.add(it) }
        if ((tile.r == 6 && tile.p.color == PieceColor.WHITE) || (tile.r == 1 && tile.p.color == PieceColor.BLACK)) {
            if (tile(tile.r + dir, tile.c)?.p == null && tile(tile.r + 2 * dir, tile.c)?.p == null) {
                tile(tile.r + 2 * dir, tile.c)?.let { moves.add(it) }
            }
        }

        listOfNotNull(tile(tile.r + dir, tile.c - 1), tile(tile.r + dir, tile.c + 1)).forEach {
            if (it.p != null && it.p.color != tile.p.color) moves.add(it)
        }
        return moves
    }

    private fun pawnThreateningTiles(tile: Tile): List<Tile> {
        val dir = if (tile.p!!.color == PieceColor.WHITE) -1 else 1
        return listOfNotNull(tile(tile.r + dir, tile.c - 1), tile(tile.r + dir, tile.c + 1))
    }

    private fun knightThreateningTiles(tile: Tile): List<Tile> {
        return listOfNotNull(
            tile(tile.r - 2, tile.c - 1),
            tile(tile.r - 2, tile.c + 1),
            tile(tile.r - 1, tile.c - 2),
            tile(tile.r - 1, tile.c + 2),
            tile(tile.r + 1, tile.c - 2),
            tile(tile.r + 1, tile.c + 2),
            tile(tile.r + 2, tile.c - 1),
            tile(tile.r + 2, tile.c + 1)
        ).filter { it.p?.color != tile.p!!.color }
    }

    private fun rookThreateningTiles(tile: Tile): List<Tile> {
        val rawLists = listOf(
            tilesInDirection(tile, 0, -1),
            tilesInDirection(tile, 0, 1),
            tilesInDirection(tile, -1, 0),
            tilesInDirection(tile, 1, 0)
        )

        return filterCapturable(tile, rawLists)
    }

    private fun bishopThreateningTiles(tile: Tile): List<Tile> {
        val rawLists = listOf(
            tilesInDirection(tile, -1, -1),
            tilesInDirection(tile, 1, 1),
            tilesInDirection(tile, -1, 1),
            tilesInDirection(tile, 1, -1)
        )

        return filterCapturable(tile, rawLists)
    }

    private fun filterCapturable(from: Tile, rawLists: List<List<Tile>>): List<Tile> {
        val filteredLists = mutableListOf<Tile>()
        for (list in rawLists) {
            for (t in list) {
                if (t.p == null) {
                    filteredLists.add(t)
                } else if (t.p.color != from.p!!.color) {
                    filteredLists.add(t)
                    break
                } else break
            }
        }

        return filteredLists
    }

    private fun tilesInDirection(fromExclusive: Tile, dx: Int, dy: Int): List<Tile> {
        val list = mutableListOf<Tile>()

        var current = tile(fromExclusive.r + dy, fromExclusive.c + dx)
        while (current != null) {
            list.add(current)
            current = tile(current.r + dy, current.c + dx)
        }

        return list
    }

    private fun queenThreateningTiles(tile: Tile): List<Tile> {
        return rookThreateningTiles(tile) + bishopThreateningTiles(tile)
    }

    private fun kingThreateningTiles(tile: Tile): List<Tile> {
        return listOfNotNull(
            tile(tile.r - 1, tile.c - 1),
            tile(tile.r - 1, tile.c),
            tile(tile.r - 1, tile.c + 1),
            tile(tile.r,     tile.c - 1),
            tile(tile.r,     tile.c + 1),
            tile(tile.r + 1, tile.c - 1),
            tile(tile.r + 1, tile.c),
            tile(tile.r + 1, tile.c + 1)
        ).filter { it.p?.color != tile.p!!.color }
    }
}