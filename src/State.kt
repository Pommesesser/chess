fun initialState(): State {
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

    return State(tiles)
}

fun inBounds(r: Int, c: Int): Boolean = r in 0..7 && c in 0..7

data class State(val tiles: List<Tile>) {
    private fun tile(r: Int, c: Int): Tile? {
        return tiles.find { it.r == r && it.c == c }
    }

    private fun king(color: PieceColor): Tile {
        return pieces(color).find { it.p?.type == PieceType.KING }!!
    }

    private fun pieces(color: PieceColor): List<Tile> {
        tiles.filter { it.p != null }
            .filter { it.p?.color == color }
    }

    private fun threatenedBy(target: Tile, threateningColor: PieceColor): Boolean {
        return pieces(threateningColor).flatMap { threatenedTiles(it) }
            .contains(target)
    }

    private fun movePutsKingInCheck(from: Tile, to: Tile): Boolean {
        val kingTile = pieces(from.p!!.color)
    }

    fun move(from: Tile, to: Tile) {
        to.p = from.p
        from.p = null
    }

    fun legalMoves(tile: Tile): List<Tile> {
        return tile.p?.let {
            when (it.type) {
                PieceType.PAWN -> pawnLegalTiles(tile)
                else -> threatenedTiles(tile)
            }.filter { !movePutsKingInCheck(tile, it) }
        } ?: emptyList()
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

    private fun pawnLegalTiles(tile: Tile): List<Tile> {
        val movementTiles = mutableListOf<Tile>()

        when (tile.p!!.color) {
            PieceColor.WHITE -> {
                val nextTile = tile(tile.r - 1, tile.c)
                nextTile?.let {
                    if (nextTile.p == null) {
                        movementTiles.add(it)
                        if (tile.r == 6) {
                            val firstMoveExtraTile = tile(tile.r - 2, tile.c)
                            firstMoveExtraTile?.let {
                                if (firstMoveExtraTile.p == null)
                                    movementTiles.add(it)
                            }
                        }
                    }
                }
            }
            PieceColor.BLACK -> {
                val nextTile = tile(tile.r + 1, tile.c)
                nextTile?.let {
                    if (nextTile.p == null) {
                        movementTiles.add(it)
                        if (tile.r == 1) {
                            val firstMoveExtraTile = tile(tile.r + 2, tile.c)
                            firstMoveExtraTile?.let {
                                if (firstMoveExtraTile.p == null)
                                    movementTiles.add(it)
                            }
                        }
                    }
                }
            }
        }

        return movementTiles + pawnCapturableTiles(tile)
    }

    private fun pawnThreateningTiles(tile: Tile): List<Tile> {
        return when(tile.p!!.color) {
            PieceColor.WHITE -> {
                listOfNotNull(
                    tile(tile.r - 1, tile.c - 1),
                    tile(tile.r - 1, tile.c + 1)
                )
            }
            PieceColor.BLACK -> {
                listOfNotNull(
                    tile(tile.r + 1, tile.c - 1),
                    tile(tile.r + 1, tile.c + 1)
                )
            }
        }.filter { it.p == null || it.p!!.color != tile.p!!.color }
    }

    private fun pawnCapturableTiles(tile: Tile): List<Tile> = pawnThreateningTiles(tile).filter { it.p != null }

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
        return listOf(
            tilesInDirection(tile, 0, -1),
            tilesInDirection(tile, 0, 1),
            tilesInDirection(tile, -1, 0),
            tilesInDirection(tile, 1, 0)
        ).flatMap { dir ->
            dir.takeWhile { it.p == null } +
                    dir.firstOrNull { it.p != null && it.p!!.color != tile.p!!.color }.let { listOfNotNull(it) }
        }
    }

    private fun bishopThreateningTiles(tile: Tile): List<Tile> {
        return listOf(
            tilesInDirection(tile, -1, -1),
            tilesInDirection(tile, 1, 1),
            tilesInDirection(tile, -1, 1),
            tilesInDirection(tile, 1, -1)
        ).flatMap { dir ->
            dir.takeWhile { it.p == null } +
                    dir.firstOrNull { it.p != null && it.p!!.color != tile.p!!.color }.let { listOfNotNull(it) }
        }
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