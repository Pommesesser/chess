fun initialGameState(): GameState {
    val board = Array(8) { arrayOfNulls<Piece>(8) }

    for (c in 0..7) {
        board[1][c] = Piece(PieceType.PAWN, PieceColor.BLACK, 1, c)
        board[6][c] = Piece(PieceType.PAWN, PieceColor.WHITE, 6, c)
    }

    board[0][0] = Piece(PieceType.ROOK, PieceColor.BLACK, 0, 0)
    board[0][7] = Piece(PieceType.ROOK, PieceColor.BLACK, 0, 7)
    board[7][0] = Piece(PieceType.ROOK, PieceColor.WHITE, 7, 0)
    board[7][7] = Piece(PieceType.ROOK, PieceColor.WHITE, 7, 7)

    board[0][1] = Piece(PieceType.KNIGHT, PieceColor.BLACK, 0, 1)
    board[0][6] = Piece(PieceType.KNIGHT, PieceColor.BLACK, 0, 6)
    board[7][1] = Piece(PieceType.KNIGHT, PieceColor.WHITE, 7, 1)
    board[7][6] = Piece(PieceType.KNIGHT, PieceColor.WHITE, 7, 6)

    board[0][2] = Piece(PieceType.BISHOP, PieceColor.BLACK, 0, 2)
    board[0][5] = Piece(PieceType.BISHOP, PieceColor.BLACK, 0, 5)
    board[7][2] = Piece(PieceType.BISHOP, PieceColor.WHITE, 7, 2)
    board[7][5] = Piece(PieceType.BISHOP, PieceColor.WHITE, 7, 5)

    board[0][3] = Piece(PieceType.QUEEN, PieceColor.BLACK, 0, 3)
    board[7][3] = Piece(PieceType.QUEEN, PieceColor.WHITE, 7, 3)

    board[0][4] = Piece(PieceType.KING, PieceColor.BLACK, 0, 4)
    board[7][4] = Piece(PieceType.KING, PieceColor.WHITE, 7, 4)

    return GameState(board)
}

fun testGameState(): GameState {
    val board = Array(8) { arrayOfNulls<Piece>(8) }

    board[0][4] = Piece(PieceType.KING, PieceColor.BLACK, 0, 4)
    board[1][2] = Piece(PieceType.ROOK, PieceColor.BLACK, 1, 2)
    board[2][5] = Piece(PieceType.KNIGHT, PieceColor.BLACK, 2, 5)
    board[3][1] = Piece(PieceType.PAWN, PieceColor.BLACK, 3, 1)
    board[3][3] = Piece(PieceType.QUEEN, PieceColor.BLACK, 3, 3)

    board[5][4] = Piece(PieceType.QUEEN, PieceColor.WHITE, 5, 4)
    board[7][4] = Piece(PieceType.KING, PieceColor.WHITE, 7, 4)
    board[6][5] = Piece(PieceType.ROOK, PieceColor.WHITE, 6, 5)
    board[5][2] = Piece(PieceType.BISHOP, PieceColor.WHITE, 5, 2)
    board[4][6] = Piece(PieceType.PAWN, PieceColor.WHITE, 4, 6)
    board[5][7] = Piece(PieceType.KNIGHT, PieceColor.WHITE, 5, 7)

    return GameState(board)
}
class GameState(val data: Array<Array<Piece?>>) {
    fun legalMoves(r: Int, c: Int): List<Pair<Int, Int>> {
        val piece = data[r][c] ?: return emptyList()
        return when (piece.type) {
            PieceType.PAWN -> legalPawnMoves(r, c)
            PieceType.ROOK -> legalRookMoves(r, c)
            PieceType.KNIGHT -> legalKnightMoves(r, c)
            PieceType.BISHOP -> legalBishopMoves(r, c)
            PieceType.QUEEN -> legalQueenMoves(r, c)
            PieceType.KING -> legalKingMoves(r, c)
        }
    }

    fun legalPawnMoves(r: Int, c: Int): List<Pair<Int, Int>> {
        val piece = data[r][c]!!

        val rawCoordinates = if (piece.color == PieceColor.WHITE) {
            if (r == 6)
                arrayOf(r - 1 to c, r - 2 to c)
            else
                arrayOf(r - 1 to c)
        } else {
            if (r == 1)
                arrayOf(r + 1 to c, r + 2 to c)
            else
                arrayOf(r + 1 to c)
        }

        val inBoundsCoordinates = rawCoordinates.filter { inBounds(it.first, it.second) }

        val legalMoves = mutableListOf<Pair<Int, Int>>()
        for (p in inBoundsCoordinates) {
            if (data[p.first][p.second] != null)
                break
            legalMoves.add(p)
        }

        val captureOffsets = if (piece.color == PieceColor.WHITE)
            listOf(-1 to -1, -1 to 1)
        else
            listOf(1 to -1, 1 to 1)

        for ((dr, dc) in captureOffsets) {
            val tr = r + dr
            val tc = c + dc
            if (tr in 0..7 && tc in 0..7) {
                val target = data[tr][tc]
                if (target != null && target.color != piece.color)
                    legalMoves.add(tr to tc)
            }
        }

        return legalMoves
    }

    fun legalRookMoves(r: Int, c: Int): List<Pair<Int, Int>> {
        val piece = data[r][c]!!
        val legalMoves = mutableListOf<Pair<Int, Int>>()

        for (rr in r + 1..7) {
            if (data[rr][c] == null) legalMoves.add(rr to c)
            else if (data[rr][c]!!.color != piece.color) {
                legalMoves.add(rr to c); break
            } else break
        }

        for (rr in r - 1 downTo 0) {
            if (data[rr][c] == null) legalMoves.add(rr to c)
            else if (data[rr][c]!!.color != piece.color) {
                legalMoves.add(rr to c); break
            } else break
        }

        for (cc in c + 1..7) {
            if (data[r][cc] == null) legalMoves.add(r to cc)
            else if (data[r][cc]!!.color != piece.color) {
                legalMoves.add(r to cc); break
            } else break
        }

        for (cc in c - 1 downTo 0) {
            if (data[r][cc] == null) legalMoves.add(r to cc)
            else if (data[r][cc]!!.color != piece.color) {
                legalMoves.add(r to cc); break
            } else break
        }

        return legalMoves
    }

    fun legalKnightMoves(r: Int, c: Int): List<Pair<Int, Int>> {
        val piece = data[r][c]!!

        return listOf(
            r + 2 to c + 1,
            r + 2 to c - 1,
            r - 2 to c + 1,
            r - 2 to c - 1,
            r + 1 to c + 2,
            r + 1 to c - 2,
            r - 1 to c + 2,
            r - 1 to c - 2
        )
            .filter { inBounds(it.first, it.second) }
            .filter {
                val target = data[it.first][it.second]
                target == null || target.color != piece.color
            }
    }

    fun legalBishopMoves(r: Int, c: Int): List<Pair<Int, Int>> {
        val piece = data[r][c]!!
        val legalMoves = mutableListOf<Pair<Int, Int>>()

        for (i in 1..7) {
            val tr = r - i
            val tc = c - i
            if (!inBounds(tr, tc)) break
            val target = data[tr][tc]
            if (target == null) legalMoves.add(tr to tc)
            else if (target.color != piece.color) {
                legalMoves.add(tr to tc); break
            } else break
        }

        for (i in 1..7) {
            val tr = r + i
            val tc = c - i
            if (!inBounds(tr, tc)) break
            val target = data[tr][tc]
            if (target == null) legalMoves.add(tr to tc)
            else if (target.color != piece.color) {
                legalMoves.add(tr to tc); break
            } else break
        }

        for (i in 1..7) {
            val tr = r + i
            val tc = c + i
            if (!inBounds(tr, tc)) break
            val target = data[tr][tc]
            if (target == null) legalMoves.add(tr to tc)
            else if (target.color != piece.color) {
                legalMoves.add(tr to tc); break
            } else break
        }

        for (i in 1..7) {
            val tr = r - i
            val tc = c + i
            if (!inBounds(tr, tc)) break
            val target = data[tr][tc]
            if (target == null) legalMoves.add(tr to tc)
            else if (target.color != piece.color) {
                legalMoves.add(tr to tc); break
            } else break
        }

        return legalMoves
    }

    fun legalQueenMoves(r: Int, c: Int): List<Pair<Int, Int>> {
        return legalRookMoves(r, c) + legalBishopMoves(r, c)
    }

    fun legalKingMoves(r: Int, c: Int): List<Pair<Int, Int>> {
        val piece = data[r][c]!!

        return listOf(
            r - 1 to c - 1,
            r - 1 to c,
            r - 1 to c + 1,
            r to c - 1,
            r to c + 1,
            r + 1 to c - 1,
            r + 1 to c,
            r + 1 to c + 1
        )
            .filter { inBounds(it.first, it.second) }
            .filter {
                val target = data[it.first][it.second]
                target == null || target.color != piece.color
            }
    }

    fun move(piece: Piece, r: Int, c: Int) {
        require(inBounds(r, c))
        data[r][c] = piece
        data[piece.r][piece.c] = null
        piece.r = r
        piece.c = c
    }

    fun threatenedBy(r: Int, c: Int, color: PieceColor): Boolean {
        require(inBounds(r, c))
        TODO()
    }

    fun pieces(): List<Piece> = data.flatMap { it.filterNotNull() }
    fun inBounds(r: Int, c: Int): Boolean = r in 0..7 && c in 0..7
}